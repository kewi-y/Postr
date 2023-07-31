package com.gprod.mediaio.models.user;

import com.gprod.mediaio.models.album.Album;

import java.util.ArrayList;

public class User {
    private String id,username,profilename,bio,profilePhotoDownloadUri;
    private long subscribersCount,subscriptionsCount,publicationsCount;
    private ArrayList<String> subscribers;
    private ArrayList<String> subscriptions;
    private ArrayList<String> favoritesPostList;
    private ArrayList<Album> favoritesAlbumList;
    public User(String id, String username,String profilename,String bio,String profilePhotoDownloadUri){
        this.id = id;
        this.username = username;
        this.profilename = profilename;
        this.bio = bio;
        this.profilePhotoDownloadUri = profilePhotoDownloadUri;
    }

    public void setProfilePhotoDownloadUri(String profilePhotoDownloadUri){
        this.profilePhotoDownloadUri = profilePhotoDownloadUri;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilename() {
        return profilename;
    }

    public void setProfilename(String profilename) {
        this.profilename = profilename;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePhotoDownloadUri() {
        return profilePhotoDownloadUri;
    }

    public long getSubscribersCount() {
        return subscribersCount;
    }

    public void setSubscribersCount(long subscribersCount) {
        this.subscribersCount = subscribersCount;
    }

    public long getSubscriptionsCount() {
        return subscriptionsCount;
    }

    public void setSubscriptionsCount(long subscriptionsCount) { this.subscriptionsCount = subscriptionsCount; }

    public long getPublicationsCount() {
        return publicationsCount;
    }

    public void setPublicationsCount(long publicationsCount) {
        this.publicationsCount = publicationsCount;
    }

    public ArrayList<String> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(ArrayList<String> subscribers) {
        this.subscribers = subscribers;
    }

    public ArrayList<String> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(ArrayList<String> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public ArrayList<String> getFavoritesPostList() {
        return favoritesPostList;
    }

    public void setFavoritesPostList(ArrayList<String> favoritesPostList) {
        this.favoritesPostList = favoritesPostList;
    }

    public ArrayList<Album> getFavoritesAlbumList() {
        return favoritesAlbumList;
    }

    public void setFavoritesAlbumList(ArrayList<Album> favoritesAlbumList) {
        this.favoritesAlbumList = favoritesAlbumList;
    }
}
