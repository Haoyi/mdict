package com.knziha.plod.dictionary;
//store key_block's summary and itself
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
	public String first_word;
	public String last_word;
	public long key_block_compressed_size_accumulator;
	public long comp_size;
	public long decomp_size;
    public long num_entries;
    public long num_entries_accumulator;
    public String[] key;
    public long[] offset;
	//public byte[] key_block_data;
    public void ini(){
        key =new String[(int) num_entries];
        offset =new long[(int) num_entries];
    }
}
