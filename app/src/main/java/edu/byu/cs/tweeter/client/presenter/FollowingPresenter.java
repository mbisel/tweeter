package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter {

    private static final int PAGE_SIZE = 10;

    private View view;
    private FollowService service;

    private User lastFollowee;

    private boolean hasMorePages;
    private boolean isLoading = false;

    public interface View {
        void displayMessage(String message);
        void setLoadingFooter(boolean value);
        void addFollowees(List<User> followees);
    }

    public FollowingPresenter(View view){
        this.view = view;
        service = new FollowService();
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void loadMoreItems(User user) {
        isLoading = true;
        view.setLoadingFooter(true);
        service.loadMoreFollowingItems(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastFollowee, new GetFollowingObserver());
    }

    private class GetFollowingObserver implements FollowService.GetFollowingObserver {

        @Override
        public void addFollowees(List<User> followees, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastFollowee = (followees.size() > 0) ? followees.get(followees.size() - 1) : null;
            view.addFollowees(followees);
            FollowingPresenter.this.hasMorePages = hasMorePages;

        }

        @Override
        public void displayErrorMessage(String message) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage("Failed to get following: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage("Failed to get following because of exception: " + ex.getMessage());

        }
    }

}
