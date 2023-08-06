package com.gprod.mediaio.services.database.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gprod.mediaio.enums.post.PostTypes;
import com.gprod.mediaio.interfaces.services.database.CheckingForUniqueCallback;
import com.gprod.mediaio.interfaces.services.database.DeletingPostCallback;
import com.gprod.mediaio.interfaces.services.database.GettingPostByIdCallback;
import com.gprod.mediaio.interfaces.services.database.GettingPostListCallback;
import com.gprod.mediaio.interfaces.services.database.GettingTagByStringTagCallback;
import com.gprod.mediaio.interfaces.services.database.GettingUserByIdCallback;
import com.gprod.mediaio.interfaces.services.database.SearchingTagCallback;
import com.gprod.mediaio.interfaces.services.database.SearchingUserCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingPostCallback;
import com.gprod.mediaio.interfaces.services.database.UpdatingUserCallback;
import com.gprod.mediaio.models.album.Album;
import com.gprod.mediaio.models.post.Comment;
import com.gprod.mediaio.models.post.ImagePost;
import com.gprod.mediaio.models.post.Post;
import com.gprod.mediaio.models.user.User;
import com.gprod.mediaio.models.post.Tag;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDatabaseService {
    private static FirebaseDatabaseService instance;
    private DatabaseReference realtimeDatabaseReference;
    private CollectionReference firestoreDatabaseReference;
    private FirebaseFirestore firestoreDatabase;
    private FirebaseDatabase realtimeDatabase;
    public static FirebaseDatabaseService  getInstance(){
        if(instance == null ){
            instance = new FirebaseDatabaseService();
        }
        return instance;
    }
    private FirebaseDatabaseService(){
        firestoreDatabase = FirebaseFirestore.getInstance();
        realtimeDatabase = FirebaseDatabase.getInstance();
    }

    public void writeUser(User user, OnSuccessListener onSuccessListener){
        Log.d("MY LOGS","Writing user >>: " + getUserHashMap(user));
        firestoreDatabase.collection("users").add(getUserHashMap(user)).addOnSuccessListener(onSuccessListener);
    }

    public void getUserById(String id, GettingUserByIdCallback gettingUserByIdCallback){
        firestoreDatabaseReference = firestoreDatabase.collection("users");
        com.google.firebase.firestore.Query databaseSortingQuery = firestoreDatabaseReference.whereEqualTo("id",id);
        databaseSortingQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        User user = getUserFromMap(document.getData());
                        gettingUserByIdCallback.onSuccess(user);
                    }
                }
                else {
                    gettingUserByIdCallback.onFailure();
                }
            }
        });
    }

    public void writePost(Post post, OnSuccessListener onSuccessListener,OnFailureListener onFailureListener){
        firestoreDatabaseReference = firestoreDatabase.collection("posts");
        firestoreDatabaseReference.add(getPostHashMap(post)).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void getPostListByAuthorId(String authorId, GettingPostListCallback gettingPostListCallback){
        ArrayList<Post> posts = new ArrayList<>();
        firestoreDatabaseReference = firestoreDatabase.collection("posts");
        com.google.firebase.firestore.Query sortingByAuthorIdQuery = firestoreDatabaseReference.whereEqualTo("authorId",authorId);
        sortingByAuthorIdQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        posts.add(getPostFormMap(document.getData()));
                    }
                    gettingPostListCallback.onSuccess(posts);
                }
                else {
                    gettingPostListCallback.onFailure();
                }
            }
        });
    }
    public void getPostListByTag(String tag,GettingPostListCallback gettingPostListCallback){
        ArrayList<Post> posts = new ArrayList<>();
        firestoreDatabaseReference = firestoreDatabase.collection("posts");
        com.google.firebase.firestore.Query sortingByTagQuery = firestoreDatabaseReference.whereArrayContains("stringTagList",tag);
        sortingByTagQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        posts.add(getPostFormMap(document.getData()));
                    }
                    gettingPostListCallback.onSuccess(posts);
                }
                else {
                    gettingPostListCallback.onFailure();
                }
            }
        });
    }

     public void updateUser(User user, UpdatingUserCallback updatingUserCallback){
        firestoreDatabaseReference = firestoreDatabase.collection("users");
        com.google.firebase.firestore.Query sortingByIdQuery = firestoreDatabaseReference.whereEqualTo("id",user.getId());
        sortingByIdQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(document.get("id").equals(user.getId())){
                            DocumentReference userDocumentReference = document.getReference();
                            userDocumentReference.set(getUserHashMap(user)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        updatingUserCallback.onSuccess(user);
                                    }
                                    else {
                                        updatingUserCallback.onFailure(task.getException().getMessage());
                                    }
                                }
                            });
                        }
                    }
                }
                else {
                    updatingUserCallback.onFailure(task.getException().getMessage());
                }
            }
        });

     }
     public void checkProfilenameForUnique(String profilename, CheckingForUniqueCallback checkingForUniqueCallback){
        realtimeDatabaseReference = realtimeDatabase.getReference("users");
        Query sortingByProfilenameQuery = realtimeDatabaseReference.orderByChild("profilename").equalTo(profilename).limitToFirst(1);
        sortingByProfilenameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() == 0){
                    checkingForUniqueCallback.isUnique();
                }
                else {
                    checkingForUniqueCallback.isNotUnique();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
     }
     public void getPostById(String postId, GettingPostByIdCallback gettingPostByIdCallback){
        firestoreDatabaseReference = firestoreDatabase.collection("posts");
        com.google.firebase.firestore.Query sortingByIdQuery = firestoreDatabaseReference.whereEqualTo("postId",postId);
        sortingByIdQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d("MY LOGS","Parse post by id ");
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Post post = getPostFormMap(document.getData());
                        gettingPostByIdCallback.onSuccess(post);
                    }
                }
                else {
                    gettingPostByIdCallback.onFailure();
                }
            }
        });
     }
     public void updatePost(Post post, UpdatingPostCallback updatingPostCallback){
        firestoreDatabaseReference = firestoreDatabase.collection("posts");
        com.google.firebase.firestore.Query sortingByIdQuery = firestoreDatabaseReference.whereEqualTo("postId",post.getPostId());
        sortingByIdQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(document.get("postId").equals(post.getPostId())){
                            DocumentReference updatablePostDocumentReference = document.getReference();
                            updatablePostDocumentReference.set(getPostHashMap(post)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        updatingPostCallback.onSuccess(post);
                                    }
                                    else {
                                        updatingPostCallback.onFailure();
                                    }
                                }
                            });
                        }
                    }
                }
                else {
                    updatingPostCallback.onFailure();
                }
            }
        });
     }
     public void addTag(String hashtag){
        realtimeDatabaseReference = realtimeDatabase.getReference("tags");
        Tag tag = new Tag(hashtag);
        realtimeDatabaseReference.push().setValue(tag).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("MY LOGS",e.toString());
            }
        });
     }

     public void updateTag(Tag tag){
        realtimeDatabaseReference = realtimeDatabase.getReference("tags");
        Query sortingByTag = realtimeDatabaseReference.orderByChild("stringTag").equalTo(tag.getStringTag()).limitToFirst(1);
        sortingByTag.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot childSnapshot : snapshot.getChildren()){
                    if(childSnapshot.child("stringTag").getValue(String.class).equals(tag.getStringTag())){
                        childSnapshot.getRef().setValue(tag);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
     }

     public void checkTagForUnique(String stringTag, CheckingForUniqueCallback checkingForUniqueCallback){
        realtimeDatabaseReference = realtimeDatabase.getReference("tags");
        Query sortingByTag = realtimeDatabaseReference.orderByChild("stringTag").equalTo(stringTag).limitToFirst(1);
        sortingByTag.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() == 0){
                    checkingForUniqueCallback.isUnique();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
     }
     public void searchTag(String searchQueryString, SearchingTagCallback searchingTagCallback){
        realtimeDatabaseReference = realtimeDatabase.getReference("tags");
        Query searchQuery = realtimeDatabaseReference.orderByChild("stringTag").startAt(searchQueryString);
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Tag> foundedTags = new ArrayList<>();
                for(DataSnapshot childSnapshot : snapshot.getChildren()){
                    Tag tag = new Tag(childSnapshot.child("stringTag").getValue(String.class),
                            childSnapshot.child("viewCount").getValue(Long.class));
                    if(tag.getStringTag().contains(searchQueryString)) {
                        foundedTags.add(tag);
                    }
                }
                searchingTagCallback.onResult(foundedTags);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
     }
     public void getTagByStringTag(String stringTag, GettingTagByStringTagCallback gettingTagByStringTagCallback){
         realtimeDatabaseReference = realtimeDatabase.getReference("tags");
         Query searchQuery = realtimeDatabaseReference.orderByChild("stringTag").startAt(stringTag);
         searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 for(DataSnapshot childSnapshot : snapshot.getChildren()){
                     Tag tag = new Tag(childSnapshot.child("stringTag").getValue(String.class),
                             childSnapshot.child("viewCount").getValue(Long.class));
                     if(tag.getStringTag().equals(stringTag)) {
                         gettingTagByStringTagCallback.onResult(tag);
                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
     }
     public void deletePost(Post post, DeletingPostCallback deletingPostCallback){
        firestoreDatabaseReference = firestoreDatabase.collection("posts");
        com.google.firebase.firestore.Query sortingByIdQuery = firestoreDatabaseReference.whereEqualTo("postId",post.getPostId());
        sortingByIdQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(document.get("postId").equals(post.getPostId())){
                            DocumentReference documentReference = document.getReference();
                            documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        deletingPostCallback.onSuccess();
                                    }
                                    else {
                                        deletingPostCallback.onFailure();
                                    }
                                }
                            });
                        }
                        else{
                            deletingPostCallback.onFailure();
                        }
                    }
                }
                else {
                    deletingPostCallback.onFailure();
                }
            }
        });

     }
     public void searchUser(String searchQueryString, SearchingUserCallback searchingUserCallback){
        ArrayList<User> foundedUsers = new ArrayList<>();
        firestoreDatabaseReference = firestoreDatabase.collection("users");
        com.google.firebase.firestore.Query searchQuery = firestoreDatabaseReference.orderBy("profilename").startAt(searchQueryString).limit(20);
        searchQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        foundedUsers.add(getUserFromMap(document.getData()));
                    }
                    searchingUserCallback.onResult(foundedUsers);
                }
            }
        });
     }
    private User parseUser(DataSnapshot childSnapshot){
        ArrayList<String> subscribers = new ArrayList<>();
        ArrayList<String> subscriptions = new ArrayList<>();
        ArrayList<String> favorites = new ArrayList<>();
        User user;
        if(childSnapshot.child("subscribers").exists()){
            for(DataSnapshot subscriberDataSnapshot : childSnapshot.child("subscribers").getChildren()){
                subscribers.add(subscriberDataSnapshot.getValue(String.class));
            }
        }
        if(childSnapshot.child("subscriptions").exists()){
            for(DataSnapshot subscriptionDataSnapshot : childSnapshot.child("subscriptions").getChildren()){
                subscriptions.add(subscriptionDataSnapshot.getValue(String.class));
            }
        }
        if(childSnapshot.child("favorites").exists()){
            for(DataSnapshot favoriteDataSnapshot : childSnapshot.child("favorites").getChildren()){
                favorites.add(favoriteDataSnapshot.getValue(String.class));
            }
        }
        user = new User(childSnapshot.child("id").getValue(String.class),
                childSnapshot.child("username").getValue(String.class),
                childSnapshot.child("profilename").getValue(String.class),
                childSnapshot.child("bio").getValue(String.class),
                childSnapshot.child("profilePhotoDownloadUri").getValue(String.class));
        user.setSubscribersCount((long) childSnapshot.child("subscribersCount").getValue());
        user.setSubscriptionsCount((long) childSnapshot.child("subscriptionsCount").getValue());
        user.setPublicationsCount((long) childSnapshot.child("publicationsCount").getValue());
        user.setSubscribers(subscribers);
        user.setSubscriptions(subscriptions);
        user.setFavoritesPostList(favorites);
        return user;
    }
    private HashMap<String, Object> getUserHashMap(User user){
        HashMap<String, Object> userHashMap = new HashMap<>();
        HashMap<String, Object> favoritesMap = new HashMap<>();
        favoritesMap.put("albums",user.getFavoritesAlbumList());
        favoritesMap.put("posts",user.getFavoritesPostList());
        userHashMap.put("id",user.getId());
        userHashMap.put("profilename",user.getProfilename());
        userHashMap.put("username",user.getUsername());
        userHashMap.put("bio", user.getBio());
        userHashMap.put("profilePhotoDownloadUri",user.getProfilePhotoDownloadUri());
        userHashMap.put("subscribersCount",user.getSubscribersCount());
        userHashMap.put("subscriptionsCount",user.getSubscriptionsCount());
        userHashMap.put("publicationsCount",user.getPublicationsCount());
        userHashMap.put("subscribers",user.getSubscribers());
        userHashMap.put("subscriptions",user.getSubscriptions());
        userHashMap.put("favorites",favoritesMap);
        return userHashMap;
    }
    private User getUserFromMap(Map<String,Object> userMap){
        Map<String, Object> favoritesMap;
        ArrayList<Map<String, Object>> albumsMap;
        ArrayList<Album> albumList = new ArrayList<>();
        User user = new User((String) userMap.get("id"), (String)userMap.get("username"),
                (String) userMap.get("profilename"), (String)userMap.get("bio"),
                (String) userMap.get("profilePhotoDownloadUri"));
        favoritesMap =(Map<String, Object>) userMap.get("favorites");
        albumsMap = (ArrayList<Map<String, Object>>) favoritesMap.get("albums");
        user.setSubscribersCount((long) userMap.get("subscribersCount"));
        user.setSubscriptionsCount((long) userMap.get("subscriptionsCount"));
        user.setPublicationsCount((long) userMap.get("publicationsCount"));
        user.setSubscribers((ArrayList<String>) userMap.get("subscribers"));
        user.setSubscriptions((ArrayList<String>) userMap.get("subscriptions"));
        user.setFavoritesPostList((ArrayList<String>) favoritesMap.get("posts"));
        for(Map albumMap : albumsMap){
            albumList.add(new Album((String) albumMap.get("id"),(String) albumMap.get("name"),(ArrayList<String>) albumMap.get("postIdList"),
                    (String) albumMap.get("previewImageDownloadUri")));
        }
        user.setFavoritesAlbumList(albumList);
        return user;
    }
    private HashMap<String, Object> getPostHashMap(Post post) {
        HashMap<String, Object> postHashMap = new HashMap<>();
        postHashMap.put("authorId",post.getAuthorId());
        postHashMap.put("description",post.getDescription());
        postHashMap.put("postId",post.getPostId());
        postHashMap.put("postType",post.getPostType());
        postHashMap.put("comments",post.getComments());
        postHashMap.put("likes",post.getLikes());
        postHashMap.put("stringTagList",post.getStringTagList());
        postHashMap.put("timestamp",post.getTimestamp().getTime());
        if(post.getPostType() == PostTypes.IMAGE_POST){
            postHashMap.put("imageDownloadUriList",((ImagePost) post).getPostImageDownloadUriList());
        }
        //TODO: add if statements for other post types
        return postHashMap;
    }
    private Post getPostFormMap(Map<String, Object> postMap){
        ArrayList<Comment> comments = new ArrayList<>();
        ArrayList<Map> commentsMapList = (ArrayList<Map>) postMap.get("comments");
        if(commentsMapList != null) {
            for(Map commentMap : commentsMapList){
                comments.add(new Comment((String) commentMap.get("id"),(String) commentMap.get("authorId"),
                        (String) commentMap.get("authorName"),(String) commentMap.get("authorProfilePhotoDownloadUri"),
                        (String) commentMap.get("content")));
            }
        }
        if(postMap.get("postType").equals(PostTypes.IMAGE_POST.toString())){
            ImagePost post = new ImagePost((String) postMap.get("authorId"),(String) postMap.get("postId"),
                    (ArrayList<String>) postMap.get("imageDownloadUriList"),(String) postMap.get("description"),
                    (ArrayList<String>) postMap.get("likes"),comments,
                    (ArrayList<String>) postMap.get("stringTagList"));
            post.setTimestamp((long) postMap.get("timestamp"));
            return post;
        }
        //TODO: add if statements for other post types
        else {
            return null;
        }
    }
}
