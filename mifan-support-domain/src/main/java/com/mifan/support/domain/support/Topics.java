package com.mifan.support.domain.support;

/**
 * @author ZYW
 * @version 1.0
 * @since 2018-01-31
 */
public class Topics {
	private Long id;
    
	private Integer replies;
	

	public Topics() {
		super();
	}

	public Topics(Long id) {
		super();
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getReplies() {
		return replies;
	}

	public void setReplies(Integer replies) {
		this.replies = replies;
	}
	
	
}
