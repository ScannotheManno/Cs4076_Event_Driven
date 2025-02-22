import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ClientGUIController_23390573_23381272 {

    @FXML
    private TextField messageField;

    @FXML
    private TextArea chatArea;

    @FXML
    private Button sendButton;

    private Client_23390573_23381272 client;

    public void initialize() {

    }

    @FXML
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            String response = client.sendMessage(message);
            chatArea.appendText("You: " + message + "\n");
            chatArea.appendText("Server: " + response + "\n");
            messageField.clear();
        }
    }

    public void onHelloButtonClick(ActionEvent actionEvent) {
    }
}
