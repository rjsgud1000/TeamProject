package Vo;

import java.sql.Date;

public class mainVO {

	private int ma_post_id;
	private String ma_nickname;
	private int ma_category;
	private String ma_title;
	private int ma_viewcount;
	private Date ma_create_at;
	private int ma_is_deleted;
	private int like_count;
	
	public mainVO() {}

	public mainVO(int ma_post_id, String ma_nickname, int ma_category, String ma_title, int ma_viewcount,
			Date ma_create_at, int ma_is_deleted) {
		super();
		this.ma_post_id = ma_post_id;
		this.ma_nickname = ma_nickname;
		this.ma_category = ma_category;
		this.ma_title = ma_title;
		this.ma_viewcount = ma_viewcount;
		this.ma_create_at = ma_create_at;
		this.ma_is_deleted = ma_is_deleted;
	}

	public int getMa_post_id() {
		return ma_post_id;
	}

	public void setMa_post_id(int ma_post_id) {
		this.ma_post_id = ma_post_id;
	}

	public String getMa_nickname() {
		return ma_nickname;
	}

	public void setMa_nickname(String ma_nickname) {
		this.ma_nickname = ma_nickname;
	}

	public int getMa_category() {
		return ma_category;
	}

	public void setMa_category(int ma_category) {
		this.ma_category = ma_category;
	}

	public String getMa_title() {
		return ma_title;
	}

	public void setMa_title(String ma_title) {
		this.ma_title = ma_title;
	}

	public int getMa_viewcount() {
		return ma_viewcount;
	}

	public void setMa_viewcount(int ma_viewcount) {
		this.ma_viewcount = ma_viewcount;
	}

	public Date getMa_create_at() {
		return ma_create_at;
	}

	public void setMa_create_at(Date ma_create_at) {
		this.ma_create_at = ma_create_at;
	}

	public int getMa_is_deleted() {
		return ma_is_deleted;
	}

	public void setMa_is_deleted(int ma_is_deleted) {
		this.ma_is_deleted = ma_is_deleted;
	}
	
	public int getLike_count() {
	    return like_count;
	}

	public void setLike_count(int like_count) {
	    this.like_count = like_count;
	}
	
	
	
}
