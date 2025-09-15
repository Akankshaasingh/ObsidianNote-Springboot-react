package com.techm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SearchLog")
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Integer getLogId() {
		return logId;
	}
	public void setLogId(Integer logId) {
		this.logId = logId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getQueryText() {
		return queryText;
	}
	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}
	public Integer getResultCount() {
		return resultCount;
	}
	public void setResultCount(Integer resultCount) {
		this.resultCount = resultCount;
	}
	public LocalDateTime getSearchTime() {
		return searchTime;
	}
	public void setSearchTime(LocalDateTime searchTime) {
		this.searchTime = searchTime;
	}
	@Column(columnDefinition = "TEXT")
    private String queryText;

    private Integer resultCount;
    private LocalDateTime searchTime;

    
}
