package Client.Service;

import Client.Model.User;
import animatefx.animation.FadeIn;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import Database.MySqlFunction;

public class Controller {
    @FXML
    public Pane pnSignIn;
    @FXML
    public Pane pnSignUp;
    @FXML
    public Button btnSignUp;
    @FXML
    public Button getStarted;
    @FXML
    public ImageView btnBack;
    @FXML
    public TextField regUsername;
    @FXML
    public TextField regPass;
    @FXML
    public TextField regEmail;
    @FXML
    public TextField regNickName;
    @FXML
    public TextField regPhoneNo;
    @FXML
    public RadioButton male;
    @FXML
    public RadioButton female;
    @FXML
    public Label controlRegLabel;
    @FXML
    public Label success;
    @FXML
    public Label goBack;
    @FXML
    public TextField userName;
    @FXML
    public TextField passWord;
    @FXML
    public Label loginNotifier;
    @FXML
    public Label nameExists;
    @FXML
    public Label checkEmail;
    public static String username, password, gender, nickname;
    public static List<User> loggedInUser = null;
    public static List<User> users = null;

    public void registration() {
        if (!regUsername.getText().equalsIgnoreCase("") && !regPass.getText().equalsIgnoreCase("") && !regEmail.getText().equalsIgnoreCase("") && !regNickName.getText().equalsIgnoreCase("") && !regPhoneNo.getText().equalsIgnoreCase("") && (male.isSelected() || female.isSelected())) {
            // if username is already in use
            if (checkUser(regUsername.getText())) {
                if (checkEmail(regEmail.getText())) {
                    User newUser = new User();
                    newUser.icon = "1.png";
                    newUser.username = regUsername.getText();
                    newUser.password = regPass.getText();
                    newUser.email = regEmail.getText();
                    newUser.nickName = regNickName.getText();
                    newUser.phone = regPhoneNo.getText();
                    if (male.isSelected()) {
                        newUser.gender = "Male";
                    } else {
                        newUser.gender = "Female";
                    }
                    MySqlFunction.addUserToRegisterSheet(newUser);
//                    users.add(newUser);
//                    UserDataService.saveAllRegisteredUsers(users);
                    goBack.setOpacity(1);
                    success.setOpacity(1);
                    makeDefault();
                    if (controlRegLabel.getOpacity() == 1) {
                        controlRegLabel.setOpacity(0);
                    }
                    if (nameExists.getOpacity() == 1) {
                        nameExists.setOpacity(0);
                    }
                } else {
                    checkEmail.setOpacity(1);
                    setOpacity(nameExists, goBack, controlRegLabel, success);
                }
            } else {
                nameExists.setOpacity(1);
                setOpacity(success, goBack, controlRegLabel, checkEmail);
            }
        } else {
            // if some input is missing
            controlRegLabel.setOpacity(1);
            setOpacity(success, goBack, nameExists, checkEmail);
        }
    }

    private void setOpacity(Label a, Label b, Label c, Label d) {
        if (a.getOpacity() == 1 || b.getOpacity() == 1 || c.getOpacity() == 1 || d.getOpacity() == 1) {
            a.setOpacity(0);
            b.setOpacity(0);
            c.setOpacity(0);
            d.setOpacity(0);
        }
    }


    private void setOpacity(Label controlRegLabel, Label checkEmail, Label nameExists) {
        controlRegLabel.setOpacity(0);
        checkEmail.setOpacity(0);
        nameExists.setOpacity(0);
    }

    private boolean checkUser(String username) {
//        users = UserDataService.loadAllRegisteredUsers();
//        for (User user : users) {
//            if (user.username.equalsIgnoreCase(username)) {
//                return false;
//            }
//        }
//        return true;
        try{
            ResultSet resultSet = MySqlFunction.findUserbyUsername("user_info", username);
            return !resultSet.next();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    private boolean checkEmail(String email) {
//        users = UserDataService.loadAllRegisteredUsers();
//        for (User user : users) {
//            if (user.email.equalsIgnoreCase(email)) {
//                return false;
//            }
//        }
//        return true;
        try{
            ResultSet resultSet = MySqlFunction.findUserbyEmail("user_info", email);
            return !resultSet.next();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    private void makeDefault() {
        regUsername.setText("");
        regPass.setText("");
        regEmail.setText("");
        regNickName.setText("");
        regPhoneNo.setText("");
        male.setSelected(true);
        setOpacity(controlRegLabel, checkEmail, nameExists);
    }


    public void login() {
//        users = UserDataService.loadAllRegisteredUsers();
//        loggedInUser = UserDataService.loadAllOnlineUsers();
        username = userName.getText();
        password = passWord.getText();
        boolean loginState = false;
        loginNotifier.setText("Username or password is incorrect!");

        User loginUser = new User();
        try{
            ResultSet resultSet = MySqlFunction.findUserbyUsernameAndPassword("user_info", username, password);
            if(resultSet.next()){
                loginUser.icon = resultSet.getString("icon");
                loginUser.nickName = resultSet.getString("nickName");
                loginUser.username = resultSet.getString("icon");
                loginUser.password = resultSet.getString("icon");
                loginUser.email = resultSet.getString("email");
                loginUser.gender = resultSet.getString("gender");
                loginUser.phone = resultSet.getString("phone");
                loginState = true;
                nickname = loginUser.username;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        // check if already login
        try{
            ResultSet resultSet = MySqlFunction.findUserbyUsername("online_user", loginUser.username);
            if(resultSet.next()){
                loginState = false;
                System.out.println("This user has already login!");
                loginNotifier.setText("This user has already login!");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        if (loginState) {
            MySqlFunction.addUserToOnlineSheet(username);
            System.out.println(loginUser.username);
            gender = loginUser.gender;
            changeWindow();
        } else {
            loginNotifier.setOpacity(1);
        }
    }

    public void logout(){
        MySqlFunction.removeUserFromSheetByUsername("online_user", username);
    }

    public void changeWindow() {
        try {
            Stage stage = (Stage) userName.getScene().getWindow();
            Parent root = FXMLLoader.load(this.getClass().getResource("../UI/Room.fxml"));
            stage.setScene(new Scene(root, 980, 800));
            stage.setTitle("ChatChatTalk: " + username + "");
            stage.setOnCloseRequest(event -> {
                logout();
                System.exit(0);
            });
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource().equals(btnSignUp)) {
            new FadeIn(pnSignUp).play();
            pnSignUp.toFront();
        }
        if (event.getSource().equals(getStarted)) {
            new FadeIn(pnSignIn).play();
            pnSignIn.toFront();
        }
        loginNotifier.setOpacity(0);
        userName.setText("");
        passWord.setText("");
    }

    @FXML
    private void handleMouseEvent(MouseEvent event) {
        if (event.getSource() == btnBack) {
            new FadeIn(pnSignIn).play();
            pnSignIn.toFront();
        }
        regUsername.setText("");
        regPass.setText("");
        regEmail.setText("");
    }
}