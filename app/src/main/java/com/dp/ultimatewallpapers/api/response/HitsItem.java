package com.dp.ultimatewallpapers.api.response;

import com.google.gson.annotations.SerializedName;

public class HitsItem{

	@SerializedName("webformatHeight")
	private int webformatHeight;

	@SerializedName("imageWidth")
	private int imageWidth;

	@SerializedName("previewHeight")
	private int previewHeight;

	@SerializedName("webformatURL")
	private String webformatURL;

	@SerializedName("userImageURL")
	private String userImageURL;

	@SerializedName("previewURL")
	private String previewURL;

	@SerializedName("comments")
	private int comments;

	@SerializedName("type")
	private String type;

	@SerializedName("imageHeight")
	private int imageHeight;

	@SerializedName("tags")
	private String tags;

	@SerializedName("previewWidth")
	private int previewWidth;

	@SerializedName("downloads")
	private int downloads;

	@SerializedName("collections")
	private int collections;

	@SerializedName("user_id")
	private int userId;

	@SerializedName("largeImageURL")
	private String largeImageURL;

	@SerializedName("pageURL")
	private String pageURL;

	@SerializedName("id")
	private int id;

	@SerializedName("imageSize")
	private int imageSize;

	@SerializedName("webformatWidth")
	private int webformatWidth;

	@SerializedName("user")
	private String user;

	@SerializedName("views")
	private int views;

	@SerializedName("likes")
	private int likes;

	public int getWebformatHeight(){
		return webformatHeight;
	}

	public int getImageWidth(){
		return imageWidth;
	}

	public int getPreviewHeight(){
		return previewHeight;
	}

	public String getWebformatURL(){
		return webformatURL;
	}

	public String getUserImageURL(){
		return userImageURL;
	}

	public String getPreviewURL(){
		return previewURL;
	}

	public int getComments(){
		return comments;
	}

	public String getType(){
		return type;
	}

	public int getImageHeight(){
		return imageHeight;
	}

	public String getTags(){
		return tags;
	}

	public int getPreviewWidth(){
		return previewWidth;
	}

	public int getDownloads(){
		return downloads;
	}

	public int getCollections(){
		return collections;
	}

	public int getUserId(){
		return userId;
	}

	public String getLargeImageURL(){
		return largeImageURL;
	}

	public String getPageURL(){
		return pageURL;
	}

	public int getId(){
		return id;
	}

	public int getImageSize(){
		return imageSize;
	}

	public int getWebformatWidth(){
		return webformatWidth;
	}

	public String getUser(){
		return user;
	}

	public int getViews(){
		return views;
	}

	public int getLikes(){
		return likes;
	}
}