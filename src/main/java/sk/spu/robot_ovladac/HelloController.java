package sk.spu.robot_ovladac;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    SerialPort serialPort1;
    OutputStream outputStream;
    String dataBuffer;

    @FXML
    public TitledPane titledPaneSettingCom;
    @FXML
    public Button buttonOpen;
    @FXML
    public Button buttonClose;
    @FXML
    public Button buttonSend;
    @FXML
    public TitledPane titledPaneData;
    @FXML
    public TextField textFieldSendData;
    @FXML
    public TextArea textAreaData;
    @FXML
    private ComboBox comboboxComPort;
    @FXML
    private ComboBox comboboxBaudRate;
    @FXML
    private ComboBox comboboxDataBits;
    @FXML
    private ComboBox comboboxStopBits;
    @FXML
    private ComboBox comboboxParityBits;
    @FXML
    public ProgressBar progressBarComStatus;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        titledPaneSettingCom.setCollapsible(false);
        titledPaneData.setCollapsible(false);
        initCommponents();
    }

    private void initCommponents(){
        comboboxComPort.getItems().addAll(loadPorts());
        comboboxBaudRate.getItems().addAll("4800","9600","38400","57600","115200");
        comboboxDataBits.getItems().addAll("6","7","8");
        comboboxStopBits.getItems().addAll("1","1.5","2");
        comboboxParityBits.getItems().addAll("NO_PARITY","EVEN_PARITY","ODD_PARITY","MARK_PARITY","SPACE_PARITY");

        textAreaData.setEditable(false);
        comboboxBaudRate.getSelectionModel().select(1);
        comboboxDataBits.getSelectionModel().select(2);
        comboboxStopBits.getSelectionModel().select(0);
        comboboxParityBits.getSelectionModel().select(0);
        comboboxComPort.setDisable(false);
        progressBarComStatus.setProgress(0);
        comboboxComPort.setDisable(false);
        progressBarComStatus.setProgress(0);
        buttonOpen.setDisable(false);
        buttonClose.setDisable(true);
        buttonSend.setDisable(true);

        buttonOpen.getStyleClass().setAll("btn","btn-success");
        buttonClose.getStyleClass().setAll("btn","btn-default");
        buttonSend.getStyleClass().setAll("btn","btn-default");
    }

    private SerialPort[] loadPorts(){
        SerialPort []portList = SerialPort.getCommPorts();
        return portList;
    }

    public void openComPort(){
        try {
            serialPort1 = loadPorts()[comboboxComPort.getSelectionModel().getSelectedIndex()];
            serialPort1.setBaudRate(Integer.parseInt(comboboxBaudRate.getSelectionModel().getSelectedItem().toString()));
            serialPort1.setNumDataBits(Integer.parseInt(comboboxDataBits.getSelectionModel().getSelectedItem().toString()));
            serialPort1.setNumStopBits(Integer.parseInt(comboboxStopBits.getSelectionModel().getSelectedItem().toString()));
            serialPort1.setParity(comboboxParityBits.getSelectionModel().getSelectedIndex());
            serialPort1.openPort();

            if (serialPort1.isOpen()) {
                Notifications.create()
                        .title("Success")
                        .text(serialPort1.getDescriptivePortName() + " Success to OPEN")
                        .position(Pos.TOP_RIGHT)
                        .show();
                comboboxComPort.setDisable(true);
                progressBarComStatus.setProgress(1);
                buttonOpen.setDisable(true);
                buttonClose.setDisable(false);
                buttonSend.setDisable(false);

                buttonOpen.getStyleClass().setAll("btn","btn-default");
                buttonClose.getStyleClass().setAll("btn","btn-danger");
                buttonSend.getStyleClass().setAll("btn","btn-success");

                serialEventBasedReading(serialPort1);
            }
            else{
                Notifications.create()
                        .title("Error")
                        .text(serialPort1.getDescriptivePortName() + " Failed to OPEN")
                        .showError();
            }
        }
        catch (ArrayIndexOutOfBoundsException exception){
            Notifications.create()
                    .title("Error")
                    .text("Please choose port! " + exception.getMessage())
                    .showError();
        }
        catch (Exception exception){
            Notifications.create()
                    .title("Error")
                    .text(exception.getMessage())
                    .showError();
        }
    }

    public void closePort(){
        if(serialPort1.isOpen()){
            serialPort1.closePort();

            comboboxComPort.setDisable(false);
            progressBarComStatus.setProgress(0);
            buttonOpen.setDisable(false);
            buttonClose.setDisable(true);
            buttonSend.setDisable(true);

            buttonOpen.getStyleClass().setAll("btn","btn-success");
            buttonClose.getStyleClass().setAll("btn","btn-default");
            buttonSend.getStyleClass().setAll("btn","btn-default");
        }

        try {
            outputStream.close();
        }
        catch (IOException exception){
            Notifications.create()
                    .title("Error")
                    .text(exception.getMessage())
                    .showError();
        }
    }

    public void sendData(){
        outputStream = serialPort1.getOutputStream();
        String dataToSend = textFieldSendData.getText();

        try{
            outputStream.write(dataToSend.getBytes());
        }
        catch (IOException exception){
            Notifications.create()
                    .title("Error")
                    .text(exception.getMessage())
                    .showError();
        }
    }

    private void serialEventBasedReading(SerialPort activeSerialPort){
        activeSerialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                byte []newData = serialPortEvent.getReceivedData();
                for(int i=0;i<newData.length;i++){
                    dataBuffer += (char) newData[i];
                    textAreaData.setText(dataBuffer);
                }
            }
        });
    }
}