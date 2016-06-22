package com.fieldsoft.someonehavevisitedhere.vo;

public class Article {
	int Article_id;
	String Title;
	int Author_id;
	String html_Content;
	
	public Article(int id,String title,int aId,String html){
		this.Article_id = id;
		this.Title = title;
		this.Author_id = aId;
		this.html_Content = html;
	}
	
	public int getArticle_id() {
		return Article_id;
	}
	public void setArticle_id(int article_id) {
		Article_id = article_id;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public int getAuthor_id() {
		return Author_id;
	}
	public void setAuthor_id(int author_id) {
		Author_id = author_id;
	}
	public String getHtml_Content() {
		return html_Content;
	}
	public void setHtml_Content(String html_Content) {
		this.html_Content = html_Content;
	}
}
