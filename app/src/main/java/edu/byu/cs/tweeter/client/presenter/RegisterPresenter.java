package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter implements UserService.RegisterObserver {

    public interface View{
        void displayInfoMessage(String message);
        void clearInfoMessage();

        void displayErrorMessage(String message);
        void clearErrorMessage();

        void navigateToUser(User user);
    }
    private View view;

    public RegisterPresenter(RegisterPresenter.View view){
        this.view = view;
    }

    public void initiateRegistration(String firstName, String lastName, String username, String password, ImageView imageToUpload, String imageAsString){
        String message = validateRegistration(firstName, lastName, username, password, imageToUpload);

        if (message == null){

            view.clearErrorMessage();
            view.displayInfoMessage("Registering... ");
            new UserService().register(firstName, lastName, username, password, imageAsString, this); //The Presenter is the observer
        }
        else {
            view.clearInfoMessage();
            view.displayErrorMessage(message);
        }

    }
    public String validateRegistration(String firstName, String lastName, String username, String password, ImageView imageToUpload){
        if (firstName.length() == 0) {
            return "First Name cannot be empty.";
        }
        if (lastName.length() == 0) {
            return "Last Name cannot be empty.";
        }
        if (username.length() == 0) {
            return "Alias cannot be empty.";
        }
        if (username.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (username.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        if (imageToUpload.getDrawable() == null) {
            return "Profile image must be uploaded.";
        }
        return null;
    }
    @Override
    public void registrationSucceeded(User user, AuthToken authToken) {
        view.displayInfoMessage("Hello " + user.getFirstName());
        view.clearErrorMessage();
        view.navigateToUser(user);
    }

    @Override
    public void registrationFailed(String message) {
        view.clearInfoMessage();
        view.displayErrorMessage(message);
    }
}


