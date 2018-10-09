package com.xiaoyan.mdict.dictionary;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Vector;

import com.xiaoyan.mdict.dictionary.keywordSect.key_index_struct.key_index_info_struct;
import com.xiaoyan.rbtree.RBTree;



/*
	 * 
	 * -headerSect
	 * -keywordSect √
	 * -recordSect
	 *
	 * */
	public class keywordSect extends dictionaryUtility{
		headerSect i_header_sect;
		
		public keywordSect(headerSect pHeader_sect) {
			i_header_sect = pHeader_sect;
		}
		

		
		public class keyword_head_struct{
			float generated_by_engine_version;

			public keyword_head_struct(float version) {
				generated_by_engine_version = version;
			}

			public keyword_packaging num_blocks = new keyword_packaging(true, generated_by_engine_version);
			public keyword_packaging num_entries = new keyword_packaging(true, generated_by_engine_version);
			public keyword_packaging key_index_decomp_len = new keyword_packaging(true, generated_by_engine_version);
			public keyword_packaging key_index_comp_len = new keyword_packaging(true, generated_by_engine_version);
			public keyword_packaging key_blocks_len = new keyword_packaging(true, generated_by_engine_version);

			public void setKeyword_head(byte[] pValue) {
	    		ByteBuffer keyword_headerBundle = ByteBuffer.wrap(pValue);
	    		
	    		if (generated_by_engine_version >= 2) {
					num_blocks.setValue(keyword_headerBundle.getLong());
					num_entries.setValue(keyword_headerBundle.getLong());
					key_index_decomp_len.setValue(keyword_headerBundle.getLong());
					key_index_comp_len.setValue(keyword_headerBundle.getLong());
					key_blocks_len.setValue(keyword_headerBundle.getLong());
				} else {
					num_blocks.setValue(keyword_headerBundle.getInt());
					num_entries.setValue(keyword_headerBundle.getInt());
					key_index_comp_len.setValue(keyword_headerBundle.getInt());
					key_blocks_len.setValue(keyword_headerBundle.getInt());
				}
			}
			public int getKeyword_head_size() {
				if (generated_by_engine_version >=2) {
					return num_blocks.getSize()+num_entries.getSize()+key_index_decomp_len.getSize()+key_index_comp_len.getSize()+key_blocks_len.getSize();
				} else {
					return num_blocks.getSize()+num_entries.getSize()+key_index_comp_len.getSize()+key_blocks_len.getSize(); 
				}
			}
			
			
			public class keyword_packaging{
				public keyword_packaging(boolean pEndianness, float pVersion) {
					endianness = pEndianness;
					version = pVersion;
				}

				long value;
				boolean endianness; //true:bigendian,false:littleendian
				int size;
				float version;
				

				public boolean isEndianness() {
					return endianness;
				}
				public int getSize() {
					if (version >= 2) {
						return 8;
					} else {
						return 4;
					}
				}
				public long getValue() {
					if (version >= 2) {
						return value;
					}else {
						return (int) value;
					}

				}
				public void setValue(long value) {
					this.value = value;
				}
				public void setValue(int value) {
					this.value = (long)value;
				}
			}
		}



		public keyword_head_struct keyword_header = new keyword_head_struct(i_header_sect.getM_header_str_xml().getGenerated_by_engine_version());
		public keyword_head_struct getKeyword_header() {
			return keyword_header;
		}

		public packaging<Integer> checksum = new packaging<Integer>(true, 4);
		public void setChecksum(byte[] pChecksum) {
			checksum.value = setChecksumValue(pChecksum, checksum.endianness, checksum.size);
		}

		RBTree<nodeComparable<Integer, Integer>> block_id_entries_tree = new RBTree<nodeComparable<Integer, Integer>>();
	    String[] block_id_search_list = new String[(int)keyword_header.num_blocks.getValue()];
	    key_index_info_struct key_index_info_struct_list;
	    
	    
	    public class key_index_struct{
	    	
			public class key_index_info_struct{
				public key_index_info_struct(String first_word, String last_word,
						long comp_size_accumulator,
						long decomp_size) {
					this.first_word=first_word;
					this.last_word=last_word;		
					this.key_block_compressed_size_accumulator=comp_size_accumulator;		
					this.decomp_size=decomp_size;		
				}
				public key_index_info_struct(long num_entries,long num_entries_accumulator) {
					this.num_entries=num_entries;
					this.num_entries_accumulator=num_entries_accumulator;
			    }
				public key_index_info_struct() {
			    }
				long num_entries;
				long	first_size;
				String	first_word;
				long	last_size;
				String	last_word;
				long	comp_size;
				long	decomp_size;
				
				
				public long key_block_compressed_size_accumulator;
			    public long num_entries_accumulator;
			}
			
			public key_index_info_struct[] setKey_index(byte[] pValue) throws UnsupportedEncodingException {
				key_index_info_struct[] key_index_info_list = new key_index_info_struct[(int)keyword_header.num_blocks.getValue()];
				byte[] key_index_info_buffer;

		    	if(i_header_sect.getM_header_str_xml().getGenerated_by_engine_version() >= 2)
		        {
		    		byte[] verify_head = new byte[]{pValue[0],pValue[1],pValue[2],pValue[3]};
		    		assert(new String(verify_head).equals(new String(new byte[]{2,0,0,0})));

		    		//处理 Ripe128md 加密的 key_index_info_buffer
		    		if(i_header_sect.getM_header_str_xml().getEncrypted()==2){
		    			try{
		    				pValue = mdxDecrypt(pValue);
		                } catch (IOException e) {e.printStackTrace();}}
		    		
		    		key_index_info_buffer = zlibDecompress(pValue,8);
		    		
		    		packaging<Integer> checksum = new packaging<Integer>(true, 4);
		    		checksum.value = setChecksumValue((new byte[] {pValue[4],pValue[5],pValue[6],pValue[7]}),checksum.endianness,checksum.size);
		    		assert(checksum.value == calcChecksum(key_index_info_buffer)) ;

		        } else {
		            key_index_info_buffer = pValue;
		        }
		    	
		    	int key_index_num_entries_accumulation = 0;
		    	long key_block_compressed_size = 0;

		        //遍历blocks
		        int bytePointer =0 ;
		        for(int i=0;i<key_index_info_list.length;i++){
		        	int textbufferST,textbufferLn;
		            //获取开始时间
		        	block_id_entries_tree.insert(new nodeComparable<Integer, Integer>(key_index_num_entries_accumulation,i));
		            //获取结束时间

		            if(i_header_sect.getM_header_str_xml().getGenerated_by_engine_version()<2) {
			            key_index_info_list[i] = new key_index_info_struct(toInt(key_index_info_buffer,bytePointer),key_index_num_entries_accumulation);
			            bytePointer+=4;
		            }
		            else {
		            	key_index_info_list[i] = new key_index_info_struct(toLong(key_index_info_buffer,bytePointer),key_index_num_entries_accumulation);
		            	bytePointer+=8;
		            }
		            key_index_info_struct infoI = key_index_info_list[i];
		            key_index_num_entries_accumulation += infoI.num_entries;

		            int text_head_size;
		            if(i_header_sect.getM_header_str_xml().getGenerated_by_engine_version()<2)
		            	text_head_size = key_index_info_buffer[bytePointer++];
		        	else {
		        		text_head_size = toChar(key_index_info_buffer,bytePointer);
		        		bytePointer+=2;
		        	}
		        	textbufferST=bytePointer;

		            if(!i_header_sect.getM_header_str_xml().getEncoding().startsWith("UTF-16")){
		            	textbufferLn=text_head_size;
		                if(i_header_sect.getM_header_str_xml().getGenerated_by_engine_version()>=2)
		            	bytePointer++;         
		            }else{
		            	textbufferLn=text_head_size*2;
		                if(i_header_sect.getM_header_str_xml().getGenerated_by_engine_version()>=2)
		                bytePointer+=2;           
		            }

		            infoI.first_word = new String(key_index_info_buffer,textbufferST,textbufferLn,i_header_sect.getM_header_str_xml().getEncoding());
		        	bytePointer+=textbufferLn;
		        	

		            int text_tail_size;
		            if(i_header_sect.getM_header_str_xml().getGenerated_by_engine_version()<2)
		            	text_tail_size = key_index_info_buffer[bytePointer++];
		        	else {
		        		text_tail_size = toChar(key_index_info_buffer,bytePointer);
		        		bytePointer+=2;
		        	}

		            textbufferST=bytePointer;
		            if(!i_header_sect.getM_header_str_xml().getEncoding().startsWith("UTF-16")){
		            	textbufferLn=text_tail_size;
		                if(i_header_sect.getM_header_str_xml().getGenerated_by_engine_version()>=2)
		            	bytePointer++;         
		            }else{
		            	textbufferLn=text_tail_size*2;
		                if(i_header_sect.getM_header_str_xml().getGenerated_by_engine_version()>=2)
		            	bytePointer+=2;       
		            }

		            infoI.last_word = new String(key_index_info_buffer,textbufferST,text_tail_size,i_header_sect.getM_header_str_xml().getEncoding());
		        	bytePointer+=textbufferLn;


		            infoI.key_block_compressed_size_accumulator = key_block_compressed_size;
		            if(i_header_sect.getM_header_str_xml().getGenerated_by_engine_version()<2){
		            	infoI.comp_size = toInt(key_index_info_buffer,bytePointer);
		            	key_block_compressed_size += infoI.comp_size;
		            	bytePointer+=4;
		            	infoI.decomp_size = toInt(key_index_info_buffer,bytePointer);
		            	bytePointer+=4;
		            }else{
		            	infoI.comp_size = toLong(key_index_info_buffer,bytePointer);
		            	key_block_compressed_size += infoI.comp_size;
		            	bytePointer+=8;
		            	infoI.decomp_size = toLong(key_index_info_buffer,bytePointer);
		            	bytePointer+=8;
		            }

		            block_id_search_list[i] = infoI.first_word;

		        }
		        return key_index_info_list;
			}
		}
		Vector<decompress_key_block> key_blocks;
		
		private class decompress_key_block{
			long offset;
			String key;
		}
		
		public int getNumberWidth() {	
            if(i_header_sect.getM_header_str_xml().getGenerated_by_engine_version() < 2.0)
                return 4;
            else
                return 8;
		}	
	}
