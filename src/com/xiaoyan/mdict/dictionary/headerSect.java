package com.xiaoyan.mdict.dictionary;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
	 * 
	 * -headerSect √
	 * -keywordSect
	 * -recordSect
	 *
	 * */
	public class headerSect extends dictionaryUtility{
		public headerSect() {
			// TODO Auto-generated constructor stub
		}
		

		
		private packaging<Integer> m_length = new packaging<Integer>(true,4);
		
		public packaging<Integer> getLength() {
			return m_length;
		}
		public void setLength(byte[] pLength) {
			m_length.value = 0;
			
			if (m_length.endianness) {
				int index = 0;
				while (index<m_length.size) {
					m_length.value <<= 8;
					m_length.value |= (pLength[index++] & 0x000000ff);
				}
			} else {
				int index = m_length.size-1;
				while (index>=0) {
					m_length.value <<= 8;
					m_length.value |= (pLength[index--] & 0x000000ff);
				}
			}
		}

		public class header_str_xml{

			public header_str_xml() {
				// TODO Auto-generated constructor stub
			}

			private float generated_by_engine_version;
			private String required_engine_version;
			private int encrypted;
			private String encoding;
			private String format;
			private Date creation_date;
			private String compact;
			private String compat;
			private String key_case_sensitive;
			private String description;
			private String title;
			private String data_source_format;
			private HashMap<Integer,String[]> stylesheet;
			private String register_by;
			private String reg_code;
			
			public float getGenerated_by_engine_version() {
				return generated_by_engine_version;
			}

			public void setGenerated_by_engine_version(float generated_by_engine_version) {
				this.generated_by_engine_version = generated_by_engine_version;
			}

			public String getRequired_engine_version() {
				return required_engine_version;
			}

			public void setRequired_engine_version(String required_engine_version) {
				this.required_engine_version = required_engine_version;
			}

			public int getEncrypted() {
				return encrypted;
			}

			public void setEncrypted(int encrypted) {
				this.encrypted = encrypted;
			}

			public String getEncoding() {
				return encoding;
			}

			public void setEncoding(String encoding) {
				this.encoding = encoding;
				
	            if(encoding.equals("GBK")|| encoding.equals("GB2312"))
	            	this.encoding = "GB18030";
	            if(encoding.equals("UTF-16"))
	            	this.encoding = "UTF-16LE";     
	            if (encoding.equals(""))
	            	this.encoding = "UTF-8";
			}

			public String getFormat() {
				return format;
			}

			public void setFormat(String format) {
				this.format = format;
			}

			public Date getCreation_date() {
				return creation_date;
			}

			public void setCreation_date(Date creation_date) {
				this.creation_date = creation_date;
			}

			public String getCompact() {
				return compact;
			}

			public void setCompact(String compact) {
				this.compact = compact;
			}

			public String getCompat() {
				return compat;
			}

			public void setCompat(String compat) {
				this.compat = compat;
			}

			public String getKey_case_sensitive() {
				return key_case_sensitive;
			}

			public void setKey_case_sensitive(String key_case_sensitive) {
				this.key_case_sensitive = key_case_sensitive;
			}

			public String getDescription() {
				return description;
			}

			public void setDescription(String description) {
				this.description = description;
			}

			public String getTitle() {
				return title;
			}

			public void setTitle(String title) {
				this.title = title;
			}

			public String getData_source_format() {
				return data_source_format;
			}

			public void setData_source_format(String data_source_format) {
				this.data_source_format = data_source_format;
			}

			public HashMap<Integer, String[]> getStylesheet() {
				return stylesheet;
			}

			public void setStylesheet(HashMap<Integer, String[]> stylesheet) {
				this.stylesheet = stylesheet;
			}

			public String getRegister_by() {
				return register_by;
			}

			public void setRegister_by(String register_by) {
				this.register_by = register_by;
			}

			public String getReg_code() {
				return reg_code;
			}

			public void setReg_code(String reg_code) {
				this.reg_code = reg_code;
			}
			
		}
		private header_str_xml m_header_str = new header_str_xml();
		
		public header_str_xml getM_header_str_xml() {
			return m_header_str;
		}
		public void setM_header_str_xml(byte[] pHeader_str) {
    		Pattern regex = Pattern.compile("(\\w+)=\"(.*?)\"",Pattern.DOTALL);
    		String header_str = null;
			try {
				header_str = new String(pHeader_str,"UTF-16LE");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
    		Matcher m = regex.matcher(header_str);
    	    HashMap<String,String> header_str_xml_tag = new HashMap<String,String>();
    		while(m.find()) {
    			header_str_xml_tag.put(m.group(1), m.group(2));
    	      }
    		if(header_str_xml_tag.containsKey("Title")) {
    			m_header_str.setTitle(header_str_xml_tag.get("Title"));
    		}
    		m_header_str.setEncoding(header_str_xml_tag.get("Encoding"));

    		if(!header_str_xml_tag.containsKey("Encrypted") || header_str_xml_tag.get("Encrypted").equals("0") || header_str_xml_tag.get("Encrypted").equals("No")) {
    			m_header_str.setEncrypted(0);
    		}else if(header_str_xml_tag.get("Encrypted") == "1") {
    			m_header_str.setEncrypted(1);
    		}else {
    			try {
        			m_header_str.setEncrypted(Integer.valueOf(header_str_xml_tag.get("Encrypted")));
    			} catch (NumberFormatException e) {
    				m_header_str.setEncrypted(0);
    			}
    		}
            
            if(header_str_xml_tag.containsKey("StyleSheet")){
                String[] lines = header_str_xml_tag.get("StyleSheet").split("[\r\n \r \n]");
                HashMap<Integer, String[]> stylesheet = new HashMap<Integer, String[]>();
                for(int i=0;i<=lines.length-3;i+=3) {
                	stylesheet.put(i,new String[]{lines[i+1],lines[i+2]});
                }
                m_header_str.setStylesheet(stylesheet);
            }
            m_header_str.setGenerated_by_engine_version(Float.valueOf(header_str_xml_tag.get("GeneratedByEngineVersion")));
		}
		
		private packaging<Integer> m_checksum = new packaging<Integer>(false,4);


		public packaging<Integer> getChecksum() {
			return m_checksum;
		}

		public void setChecksum(byte[] pChecksum) {
			m_checksum.value = setChecksumValue(pChecksum, m_checksum.endianness, m_checksum.size);
		}
	}