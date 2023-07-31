package com.gprod.mediaio.ui.fragments.search.searchResult.hashtags;

import androidx.lifecycle.ViewModel;

import com.gprod.mediaio.models.post.Tag;
import com.gprod.mediaio.repositories.PostRepository;
import com.gprod.mediaio.repositories.SelectedTagRepository;

public class SearchResultHashtagsViewModel extends ViewModel {
    private PostRepository postRepository;
    private SelectedTagRepository selectedTagRepository;
    public SearchResultHashtagsViewModel(){
        postRepository = PostRepository.getInstance();
        selectedTagRepository = SelectedTagRepository.getInstance();
    }
    public void selectTag(Tag tag){
        selectedTagRepository.setSelectedTag(tag);
        postRepository.addViewCountForTag(tag);
    }
}