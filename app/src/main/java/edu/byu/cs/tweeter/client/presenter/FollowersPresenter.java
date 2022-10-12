package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter {

    private static final int PAGE_SIZE = 10;

    private View view;
    private FollowService service;

    private User lastFollower;

    private boolean hasMorePages;
    private boolean isLoading = false;

    public interface View {
        void displayMessage(String message);
        void setLoadingFooter(boolean value);
        void addFollowers(List<User> followers);
        void navigateToUser(User user);
    }


    public FollowersPresenter(View view){
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
        service.loadMoreFollowerItems(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastFollower, new GetFollowersObserver());
    }


    private class GetFollowersObserver implements FollowService.GetFollowersObserver{

        @Override
        public void addFollowers(List<User> followers, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;
            view.addFollowers(followers);
            FollowersPresenter.this.hasMorePages = hasMorePages;

        }

        @Override
        public void displayErrorMessage(String message) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage("Failed to get followers: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            isLoading = false;
            view.setLoadingFooter(false);
            view.displayMessage("Failed to get followers because of exception: ");
        }


    }

    public void getUser(AuthToken authToken, String alias) {
        view.displayMessage("Getting user's profile...");
        new UserService().getUser(authToken, alias, new GetUserObserver());
    }

    private class GetUserObserver implements UserService.GetUserObserver {

        @Override
        public void getUser(User user) {
            view.navigateToUser(user);
        }

        @Override
        public void userFailed(String message) {
            view.displayMessage(message);
        }

        @Override
        public void userException(String ex) {
            view.displayMessage(ex);
        }
    }

}
