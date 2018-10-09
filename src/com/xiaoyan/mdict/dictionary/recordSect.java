package com.xiaoyan.mdict.dictionary;

import java.util.Vector;

/*
	 * 
	 * -headerSect
	 * -keywordSect
	 * -recordSect √
	 *
	 * */
	public class recordSect{
		int num_blocks;
		int num_entries;
		int index_len;
		int blocks_len;
		Vector<Integer>	comp_size;
		Vector<Integer>	decomp_size;
		Vector<String>	rec_block;
	}
