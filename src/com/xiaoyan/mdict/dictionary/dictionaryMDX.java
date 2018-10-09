package com.xiaoyan.mdict.dictionary;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.knziha.plod.dictionary.key_index_info_struct;
import com.xiaoyan.rbtree.RBTree;




@SuppressWarnings({ "unused", "resource" })
public class dictionaryMDX extends dictionaryUtility{
	String dictPath;
	File dictFile;
	
    private final static String replaceReg = " |:|\\.|,|-|\'|(|)";
    private final static String emptyStr = "";
    

	RBTree<nodeComparable<Integer, Integer>> accumulation_blockId_tree = new RBTree<nodeComparable<Integer, Integer>>();
    RBTree<nodeComparable<Long, Integer>> accumulation_RecordB_tree = new RBTree<nodeComparable<Long, Integer>>();

    String[] block_id_search_list;
	
	
	public dictionaryMDX(String path){
		dictPath = path;
	}


    
	private void initDict(String path) {

    	headerSect m_header_sect = new headerSect();
    	keywordSect m_keyword_sect = new keywordSect(m_header_sect);
    	
    	
    	try {
    		dictFile = new File(path);
    		
    		if(! dictFile.exists()) {
    			return;
    		}
    		
    		DataInputStream dictStream = new DataInputStream(new FileInputStream(dictFile));
    		
        	byte[] lengthBuf = new byte[m_header_sect.getLength().size];
        	dictStream.read(lengthBuf, 0, m_header_sect.getLength().size);
    		m_header_sect.setLength(lengthBuf);
    		
        	byte[] header_strBuf = new byte[m_header_sect.getLength().value];
        	dictStream.read(header_strBuf,0, m_header_sect.getLength().value); 
        	m_header_sect.setM_header_str_xml(header_strBuf);
        	
    		byte[] checksumBuf= new byte[m_header_sect.getChecksum().size];
    		dictStream.read(checksumBuf, 0, m_header_sect.getChecksum().size);
    		m_header_sect.setChecksum(checksumBuf);
    		assert m_header_sect.getChecksum().value == (calcChecksum(header_strBuf)& 0xffffffff);
    		//TODO 这地方要增加对checksum的错误处理代码，如果出错就return返回，不执行下面

    		if (m_header_sect.getM_header_str_xml().getEncrypted() == 1) {
    			//TODO 需要处理字典加密情况 pureSalsa20.py decryption
    			System.out.println("字典是加密的，需要处理");
			}
    		

    		
    		byte[] keyword_headerBuf = new byte[m_keyword_sect.getKeyword_header().getKeyword_head_size()];    		
    		dictStream.read(keyword_headerBuf, 0, m_keyword_sect.getKeyword_header().getKeyword_head_size());
    		m_keyword_sect.getKeyword_header().setKeyword_head(keyword_headerBuf);
    		
    		dictStream.read(checksumBuf, 0, m_keyword_sect.checksum.size);
    		m_keyword_sect.setChecksum(checksumBuf);
    		assert m_keyword_sect.checksum.value == (calcChecksum(keyword_headerBuf)& 0xffffffff);




    		
    	}catch(Exception e){
    		System.out.println("字典文件出错");
    	}
    }

	
	


}