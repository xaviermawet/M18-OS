package GUI;

import Utils.*;
import java.awt.Color;
import java.io.IOException;
import java.net.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.swing.DefaultComboBoxModel;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Nakim
 */
public class MainFrame extends javax.swing.JFrame
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public MainFrame()
    {
        this.initComponents();

        this.sockV1 = null;
        this.sockV2 = null;
        this.interpreterProperties = null;
        this.isConnectedV1 = false;
        this.isConnectedV2 = false;

        // Redirect the system output to a TextArea
        TextAreaOutputStream toas = TextAreaOutputStream.getInstance(
            this.textAreaOutput);

        // Load properties file
        try
        {
            String path = System.getProperty("user.dir");
            path += System.getProperty("file.separator")
                    + "src"
                    + System.getProperty("file.separator")
                    + "GUI"
                    + System.getProperty("file.separator")
                    + "config.properties";

            Properties propConfig = PropertyLoader.load(path);

            // Set the default ip servers
            this.textFieldIPServerV1.setText(
                propConfig.getProperty("ip_server_v1", DEFAULT_IP_V1));
            this.textFieldIPServerV2.setText(
                propConfig.getProperty("ip_server_v2", DEFAULT_IP_V2));

            // Set the default port server
            this.spinnerPortServerV1.setValue(
                new Integer(propConfig.getProperty(
                    "port_server_v1", DEFAULT_PORT_V1)));
            this.spinnerPortServerV2.setValue(
                new Integer(propConfig.getProperty(
                    "port_server_v2", DEFAULT_PORT_V2)));
        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }

        this.createModels();
        this.openBible();

        // Create cipher
        this.createCiphers();

        this.showStatusV1();
        this.showStatusV2();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void createModels()
    {
        // Load properties file
        try
        {
            String path = System.getProperty("user.dir");
            path += System.getProperty("file.separator")
                    + "src"
                    + System.getProperty("file.separator")
                    + "GUI"
                    + System.getProperty("file.separator")
                    + "interpreter.properties";

            this.interpreterProperties = PropertyLoader.load(path);

            // Get all messages
            this.messagesModel = new DefaultComboBoxModel(
                this.interpreterProperties.keySet().toArray());
            this.comboBoxMessageV1.setModel(this.messagesModel);
            this.comboBoxMessageV2.setModel(this.messagesModel);
        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }
    }

    private void openBible()
    {
        // Load properties file
        try
        {
            String path = System.getProperty("user.dir");
            path += System.getProperty("file.separator")
                    + "src"
                    + System.getProperty("file.separator")
                    + "GUI"
                    + System.getProperty("file.separator")
                    + "bible.properties";

            this.bibleProperties = PropertyLoader.load(path);
        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }
    }

    private void createCiphers()
    {
        try
        {
            // Version 1
            this.symmetricCrypterV1 = new SymmetricCrypter(
                algorithm + "/" + cipherMode + "/" + padding);

            // Version 2
            this.symmetricCrypterV2 = new SymmetricCrypter(
                algorithm + "/" + cipherMode + "/" + padding);
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException ex)
        {
            this.symmetricCrypterV1 = null;
            this.symmetricCrypterV2 = null;
            System.out.println("[FAIL] Unable to create ciphers : " + ex.getMessage());
        }
    }

    private void connectToServerV1()
    {
        if (this.sockV1 != null)
            this.disconnectFromServerV1();

        int port = (int)this.spinnerPortServerV1.getValue();
        String ip = this.textFieldIPServerV1.getText();
        System.out.println("[ V1 ] Connection to server " + ip + ":" + port);

        try
        {
            this.sockV1 = new Socket(ip, port);
        }
        catch (UnknownHostException ex)
        {
            System.out.println("[FAIL] Host unreachable. Invalid IP " + ip);
        }
        catch (IOException ex)
        {
            System.out.println("[FAIL] Failed to connect");
        }
        finally
        {
            this.showStatusV1();
        }
    }

    private void connectToServerV2()
    {
        if (this.sockV2 != null)
            this.disconnectFromServerV2();

        int port = (int)this.spinnerPortServerV2.getValue();
        String ip = this.textFieldIPServerV2.getText();
        System.out.println("[ V2 ] Connection to server " + ip + ":" + port);

        try
        {
            this.sockV2 = new Socket(ip, port);
        }
        catch (UnknownHostException ex)
        {
            System.out.println("[FAIL] Host unreachable. Invalid IP " + ip);
        }
        catch (IOException ex)
        {
            System.out.println("[FAIL] Failed to connect");
        }
        finally
        {
            this.showStatusV2();
        }
    }

    private void disconnectFromServerV1()
    {
        if (this.sockV1 == null)
        {
            System.out.println("[FAIL] You are not connected to the server");
            return;
        }

        try
        {
            this.sockV1.close();
            this.sockV1 = null;
            this.symmetricCrypterV1.invalidate();
        }
        catch (Exception ex)
        {
            System.out.println("[FAIL] An error occurred disconnecting the system from the server : " + ex);
        }
        finally
        {
            this.showStatusV1();
        }
    }

    private void disconnectFromServerV2()
    {
        if (this.sockV2 == null)
        {
            System.out.println("[FAIL] You are not connected to the server");
            return;
        }

        try
        {
            this.sockV2.close();
            this.sockV2 = null;
            this.symmetricCrypterV2.invalidate();
        }
        catch (Exception ex)
        {
            System.out.println("[FAIL] An error occurred disconnecting the system from the server : " + ex);
        }
        finally
        {
            this.showStatusV2();
        }
    }

    private void showStatusV1()
    {
        this.isConnectedV1 = this.sockV1 != null && this.sockV1.isConnected();

        this.spinnerPortServerV1.setEnabled(!this.isConnectedV1);
        this.textFieldIPServerV1.setEnabled(!this.isConnectedV1);
        this.comboBoxMessageV1.setEnabled(this.isConnectedV1);
        this.buttonGenerateKeyV1.setEnabled(this.isConnectedV1);
        this.buttonSendMessageV1.setEnabled(this.isConnectedV1);

        if (this.isConnectedV1)
        {
            this.labelStatusV1.setForeground(Color.GREEN);
            this.labelStatusV1.setText("Connected");
            this.buttonConnectV1.setText("Disconnect");
        }
        else
        {
            this.labelStatusV1.setForeground(Color.RED);
            this.labelStatusV1.setText("Disconnected");
            this.buttonConnectV1.setText("Connect");
        }
    }

    private void showStatusV2()
    {
        this.isConnectedV2 = this.sockV2 != null && this.sockV2.isConnected();

        this.spinnerPortServerV2.setEnabled(!this.isConnectedV2);
        this.textFieldIPServerV2.setEnabled(!this.isConnectedV2);
        this.comboBoxMessageV2.setEnabled(this.isConnectedV2);
        this.buttonGenerateKeyV2.setEnabled(this.isConnectedV2);
        this.buttonSendMessageV2.setEnabled(this.isConnectedV2);

        if (this.isConnectedV2)
        {
            this.labelStatusV2.setForeground(Color.GREEN);
            this.labelStatusV2.setText("Connected");
            this.buttonConnectV2.setText("Disconnect");
        }
        else
        {
            this.labelStatusV2.setForeground(Color.RED);
            this.labelStatusV2.setText("Disconnected");
            this.buttonConnectV2.setText("Connect");
        }
    }

    private void generateSecretKeyV1()
    {
        System.out.println("[ V1 ] Generate new secret key");

        try
        {
            // Check if client is connected
            if (this.sockV1 == null || !this.sockV1.isConnected())
                throw new Exception("You are disconnected from server");

            // Check if ciphers exist
            if (this.symmetricCrypterV1 == null)
                throw new Exception("No crypter object available");

            /* Get bible line number before sending a query to be shure that
             * the line number is the same for the server and the client if
             * the network communication is too slow */
            String minute = new SimpleDateFormat("mm").format(new Date());
            int bibleLineNumber = Integer.parseInt(minute) % 10;

            // Send a GENERATE_KEY request
            Request query = new Request("GENERATE_KEY");
            Request reply = query.sendAndRecv(this.sockV1);

            // If client has been disconected
            if (reply.is("NO_COMMAND"))
            {
                this.disconnectFromServerV1();
                throw new Exception("Disconnected from server");
            }

            if (reply.is("GENERATE_KEY_FAIL"))
                throw new Exception(reply.getStringArg(0));

            if (!reply.is("GENERATE_KEY_ACK"))
                throw new Exception("Invalid reply : " + reply.getCommand());

            // Get bible line at "bibleLineNumber"
            String bibleLine = this.bibleProperties.getProperty(
                Integer.toString(bibleLineNumber));
            System.out.println("[ V1 ] bible line " + bibleLineNumber
                + " = " + bibleLine);

            // generate new secret key
            SecretKey newSecretKey = new SecretKeySpec(
                bibleLine.substring(bibleLineNumber,bibleLineNumber + 8)
                    .getBytes(), algorithm);

            // initialize ciphers
            this.symmetricCrypterV1.init(newSecretKey);

            System.out.println("[ V1 ] New secret key generated");
        }
        catch (Exception e)
        {
            System.out.println("[ V1 ] " + e.getMessage());
            MessageBoxes.ShowError(e.getMessage(), "Error generating secret key");
        }
    }

    private void generateSecretKeyV2()
    {
        System.out.println("[ V2 ] Generate new secret key");

        try
        {
            // Check if client is connected
            if (this.sockV2 == null || !this.sockV2.isConnected())
                throw new Exception("You are disconnected from server");

            // Check if ciphers exist
            if (this.symmetricCrypterV2 == null)
                throw new Exception("No crypter object available");

            // Create the parameter generator for a 1024-bit DH key pair
            AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
            paramGen.init(1024);

            System.out.println("[ V2 ] Generating params");

            // Generate the parameters
            AlgorithmParameters params = paramGen.generateParameters();
            //Specify parameters to use for the algorithm
            DHParameterSpec dhSpec = (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);

            System.out.println("[ V2 ] Params generated");

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
            keyGen.initialize(dhSpec);

            KeyPair keypair = keyGen.generateKeyPair();

            // Send a GENERATE_KEY request to the server with the public key bytes
            Request query = new Request("GENERATE_KEY");
            query.addArg(keypair.getPublic().getEncoded());

            System.out.println("[ V2 ] Sending public key");
            Request reply = query.sendAndRecv(this.sockV2);

            // If client has been disconected
            if (reply.is("NO_COMMAND"))
            {
                this.disconnectFromServerV2();
                throw new Exception("Disconnected from server");
            }

            if (reply.is("GENERATE_KEY_FAIL"))
                throw new Exception(reply.getStringArg(0));

            if (!reply.is("GENERATE_KEY_ACK"))
                throw new Exception("Invalid reply : " + reply.getCommand());


            // Get public key from server
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(
                reply.getArg(0));
            KeyFactory keyFact = KeyFactory.getInstance("DH");
            PublicKey publicKey = keyFact.generatePublic(x509KeySpec);

            /* Prepare to generate the secret key with the private key and
             * public key of the server
             */
            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(keypair.getPrivate());
            ka.doPhase(publicKey, true);

            // Generate new secret key and initialize crypter
            this.symmetricCrypterV2.init(ka.generateSecret("DES"));

            System.out.println("[ V2 ] New secret key generated");
        }
        catch (Exception e)
        {
            System.out.println("[ V2 ] " + e.getMessage());
            MessageBoxes.ShowError(e.getMessage(), "Error generating secret key");
        }
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonClear = new javax.swing.JButton();
        splitPane = new javax.swing.JSplitPane();
        scrollPane = new javax.swing.JScrollPane();
        textAreaOutput = new javax.swing.JTextArea();
        tabbedPane = new javax.swing.JTabbedPane();
        panelV1 = new javax.swing.JPanel();
        panelHeaderV1 = new javax.swing.JPanel();
        buttonConnectV1 = new javax.swing.JButton();
        panelHeaderBodyV1 = new javax.swing.JPanel();
        labelStatusInfoV1 = new javax.swing.JLabel();
        labelStatusV1 = new javax.swing.JLabel();
        labelIPV1 = new javax.swing.JLabel();
        textFieldIPServerV1 = new javax.swing.JTextField();
        labelPortV1 = new javax.swing.JLabel();
        spinnerPortServerV1 = new javax.swing.JSpinner();
        panelBodyV1 = new javax.swing.JPanel();
        buttonGenerateKeyV1 = new javax.swing.JButton();
        buttonSendMessageV1 = new javax.swing.JButton();
        comboBoxMessageV1 = new javax.swing.JComboBox();
        labelMessageV1 = new javax.swing.JLabel();
        panelV2 = new javax.swing.JPanel();
        panelHeaderV2 = new javax.swing.JPanel();
        buttonConnectV2 = new javax.swing.JButton();
        panelHeaderBodyV2 = new javax.swing.JPanel();
        labelStatusInfoV2 = new javax.swing.JLabel();
        labelStatusV2 = new javax.swing.JLabel();
        labelIPV2 = new javax.swing.JLabel();
        textFieldIPServerV2 = new javax.swing.JTextField();
        labelPortV2 = new javax.swing.JLabel();
        spinnerPortServerV2 = new javax.swing.JSpinner();
        panelBodyV2 = new javax.swing.JPanel();
        buttonGenerateKeyV2 = new javax.swing.JButton();
        buttonSendMessageV2 = new javax.swing.JButton();
        comboBoxMessageV2 = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Client Diffie Hellman");

        buttonClear.setText("Clear");
        buttonClear.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonClearActionPerformed(evt);
            }
        });
        getContentPane().add(buttonClear, java.awt.BorderLayout.PAGE_END);

        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        textAreaOutput.setColumns(20);
        textAreaOutput.setRows(5);
        scrollPane.setViewportView(textAreaOutput);

        splitPane.setBottomComponent(scrollPane);

        panelV1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelV1.setLayout(new java.awt.BorderLayout());

        panelHeaderV1.setLayout(new java.awt.BorderLayout());

        buttonConnectV1.setText("<state>");
        buttonConnectV1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonConnectV1ActionPerformed(evt);
            }
        });
        panelHeaderV1.add(buttonConnectV1, java.awt.BorderLayout.LINE_END);

        panelHeaderBodyV1.setLayout(new java.awt.GridLayout(3, 2));

        labelStatusInfoV1.setText("Status :");
        panelHeaderBodyV1.add(labelStatusInfoV1);

        labelStatusV1.setText("<status>");
        panelHeaderBodyV1.add(labelStatusV1);

        labelIPV1.setText("IP address :");
        panelHeaderBodyV1.add(labelIPV1);
        panelHeaderBodyV1.add(textFieldIPServerV1);

        labelPortV1.setText("Port : ");
        panelHeaderBodyV1.add(labelPortV1);

        spinnerPortServerV1.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        panelHeaderBodyV1.add(spinnerPortServerV1);

        panelHeaderV1.add(panelHeaderBodyV1, java.awt.BorderLayout.CENTER);

        panelV1.add(panelHeaderV1, java.awt.BorderLayout.PAGE_START);

        panelBodyV1.setLayout(new java.awt.GridBagLayout());

        buttonGenerateKeyV1.setText("Generate new key");
        buttonGenerateKeyV1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonGenerateKeyV1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelBodyV1.add(buttonGenerateKeyV1, gridBagConstraints);

        buttonSendMessageV1.setText("Send message");
        buttonSendMessageV1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonSendMessageV1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelBodyV1.add(buttonSendMessageV1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        panelBodyV1.add(comboBoxMessageV1, gridBagConstraints);

        labelMessageV1.setText("Message : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelBodyV1.add(labelMessageV1, gridBagConstraints);

        panelV1.add(panelBodyV1, java.awt.BorderLayout.CENTER);

        tabbedPane.addTab("Version 1 - Secret word", panelV1);

        panelV2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelV2.setLayout(new java.awt.BorderLayout());

        panelHeaderV2.setLayout(new java.awt.BorderLayout());

        buttonConnectV2.setText("<state>");
        buttonConnectV2.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonConnectV2ActionPerformed(evt);
            }
        });
        panelHeaderV2.add(buttonConnectV2, java.awt.BorderLayout.LINE_END);

        panelHeaderBodyV2.setLayout(new java.awt.GridLayout(3, 2));

        labelStatusInfoV2.setText("Status :");
        panelHeaderBodyV2.add(labelStatusInfoV2);

        labelStatusV2.setText("<status>");
        panelHeaderBodyV2.add(labelStatusV2);

        labelIPV2.setText("IP address :");
        panelHeaderBodyV2.add(labelIPV2);
        panelHeaderBodyV2.add(textFieldIPServerV2);

        labelPortV2.setText("Port : ");
        panelHeaderBodyV2.add(labelPortV2);

        spinnerPortServerV2.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        panelHeaderBodyV2.add(spinnerPortServerV2);

        panelHeaderV2.add(panelHeaderBodyV2, java.awt.BorderLayout.CENTER);

        panelV2.add(panelHeaderV2, java.awt.BorderLayout.PAGE_START);

        panelBodyV2.setLayout(new java.awt.GridBagLayout());

        buttonGenerateKeyV2.setText("Generate new key");
        buttonGenerateKeyV2.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonGenerateKeyV2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelBodyV2.add(buttonGenerateKeyV2, gridBagConstraints);

        buttonSendMessageV2.setText("Send message");
        buttonSendMessageV2.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonSendMessageV2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        panelBodyV2.add(buttonSendMessageV2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelBodyV2.add(comboBoxMessageV2, gridBagConstraints);

        panelV2.add(panelBodyV2, java.awt.BorderLayout.CENTER);

        tabbedPane.addTab("Version 2 - Diffie Hellman", panelV2);

        splitPane.setLeftComponent(tabbedPane);

        getContentPane().add(splitPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //<editor-fold defaultstate="collapsed" desc="Events management">
    private void buttonConnectV1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonConnectV1ActionPerformed
    {//GEN-HEADEREND:event_buttonConnectV1ActionPerformed
        if (this.isConnectedV1)
            this.disconnectFromServerV1();
        else
            this.connectToServerV1();
    }//GEN-LAST:event_buttonConnectV1ActionPerformed

    private void buttonClearActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonClearActionPerformed
    {//GEN-HEADEREND:event_buttonClearActionPerformed
        this.textAreaOutput.setText(null);
    }//GEN-LAST:event_buttonClearActionPerformed

    private void buttonSendMessageV1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonSendMessageV1ActionPerformed
    {//GEN-HEADEREND:event_buttonSendMessageV1ActionPerformed
        try
        {
            // Check if client is connected
            if (this.sockV1 == null || !this.sockV1.isConnected())
                throw new Exception("You are disconnected from server");

            // Check if ciphers exist
            if (this.symmetricCrypterV1 == null)
                throw new Exception("No crypter object available");

            // Check if secretKey and ciphers exist
            if (!this.symmetricCrypterV1.isValid())
                throw new Exception("A new secret key must be generated");

            // Get coded message
            String codedMessage = (String)this.comboBoxMessageV1.getSelectedItem();
            if (codedMessage == null || codedMessage.isEmpty())
                throw new Exception("Unable to send empty message");

            // Encrypt coded message
            byte[] cipherTextByteArray = this.symmetricCrypterV1.encrypt(codedMessage);

            // Send new MESSAGE query
            Request query = new Request("MESSAGE");
            query.addArg(cipherTextByteArray);
            System.out.println("[ V1 ] Encrypted message sent");
            Request reply = query.sendAndRecv(this.sockV1);

            // If client has been disconected
            if (reply.is("NO_COMMAND"))
            {
                this.disconnectFromServerV1();
                throw new Exception("Disconnected from server");
            }

            // If query failed
            if (reply.is("MESSAGE_FAIL"))
                throw new Exception("Server error : " + reply.getStringArg(0));

            if (!reply.is("MESSAGE_ACK"))
                throw new Exception("Invalid reply : " + reply.getCommand());

            // Get encoded message
            System.out.println("[ V1 ] Received reply");
            codedMessage = this.symmetricCrypterV1.decryptString(reply.getArg(0));

            System.out.println("[ V1 ] Decrypted message : " + codedMessage);
            System.out.println("[ V1 ] Real message is : " +
                this.interpreterProperties.getProperty(codedMessage, "UNKNOW"));
        }
        catch (Exception e)
        {
            System.out.println("[ V1 ] " + e.getMessage());
            MessageBoxes.ShowError(e.getMessage(), "Error sending message");
        }
    }//GEN-LAST:event_buttonSendMessageV1ActionPerformed

    private void buttonGenerateKeyV1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonGenerateKeyV1ActionPerformed
    {//GEN-HEADEREND:event_buttonGenerateKeyV1ActionPerformed
        this.generateSecretKeyV1();
    }//GEN-LAST:event_buttonGenerateKeyV1ActionPerformed

    private void buttonSendMessageV2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonSendMessageV2ActionPerformed
    {//GEN-HEADEREND:event_buttonSendMessageV2ActionPerformed
        try
        {
            // Check if client is connected
            if (this.sockV2 == null || !this.sockV2.isConnected())
            throw new Exception("You are disconnected from server");

            // Check if ciphers exist
            if (this.symmetricCrypterV2 == null)
            throw new Exception("No crypter object available");

            // Check if secretKey and ciphers exist
            if (!this.symmetricCrypterV2.isValid())
            throw new Exception("A new secret key must be generated");

            // Get coded message
            String codedMessage = (String)this.comboBoxMessageV2.getSelectedItem();
            if (codedMessage == null || codedMessage.isEmpty())
            throw new Exception("Unable to send empty message");

            // Encrypt coded message
            byte[] cipherTextByteArray = this.symmetricCrypterV2.encrypt(codedMessage);

            // Send new MESSAGE query
            Request query = new Request("MESSAGE");
            query.addArg(cipherTextByteArray);
            System.out.println("[ V2 ] Encrypted message sent");
            Request reply = query.sendAndRecv(this.sockV2);

            // If client has been disconected
            if (reply.is("NO_COMMAND"))
            {
                this.disconnectFromServerV2();
                throw new Exception("Disconnected from server");
            }

            // If query failed
            if (reply.is("MESSAGE_FAIL"))
            throw new Exception("Server error : " + reply.getStringArg(0));

            if (!reply.is("MESSAGE_ACK"))
            throw new Exception("Invalid reply : " + reply.getCommand());

            // Get encoded message
            System.out.println("[ V2 ] Received reply");
            codedMessage = this.symmetricCrypterV2.decryptString(reply.getArg(0));

            System.out.println("[ V2 ] Decrypted message : " + codedMessage);
            System.out.println("[ V2 ] Real message is : " +
                this.interpreterProperties.getProperty(codedMessage, "UNKNOW"));
        }
        catch (Exception e)
        {
            System.out.println("[ V2 ] " + e.getMessage());
            MessageBoxes.ShowError(e.getMessage(), "Error sending message");
        }
    }//GEN-LAST:event_buttonSendMessageV2ActionPerformed

    private void buttonGenerateKeyV2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonGenerateKeyV2ActionPerformed
    {//GEN-HEADEREND:event_buttonGenerateKeyV2ActionPerformed
        this.generateSecretKeyV2();
    }//GEN-LAST:event_buttonGenerateKeyV2ActionPerformed

    private void buttonConnectV2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonConnectV2ActionPerformed
    {//GEN-HEADEREND:event_buttonConnectV2ActionPerformed
        if (this.isConnectedV2)
        this.disconnectFromServerV2();
        else
        this.connectToServerV2();
    }//GEN-LAST:event_buttonConnectV2ActionPerformed
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Main">
    public static void main(String args[])
    {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException | InstantiationException |
            IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
            System.err.println(ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() ->
        {
            new MainFrame().setVisible(true);
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Generated Widgets">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClear;
    private javax.swing.JButton buttonConnectV1;
    private javax.swing.JButton buttonConnectV2;
    private javax.swing.JButton buttonGenerateKeyV1;
    private javax.swing.JButton buttonGenerateKeyV2;
    private javax.swing.JButton buttonSendMessageV1;
    private javax.swing.JButton buttonSendMessageV2;
    private javax.swing.JComboBox comboBoxMessageV1;
    private javax.swing.JComboBox comboBoxMessageV2;
    private javax.swing.JLabel labelIPV1;
    private javax.swing.JLabel labelIPV2;
    private javax.swing.JLabel labelMessageV1;
    private javax.swing.JLabel labelPortV1;
    private javax.swing.JLabel labelPortV2;
    private javax.swing.JLabel labelStatusInfoV1;
    private javax.swing.JLabel labelStatusInfoV2;
    private javax.swing.JLabel labelStatusV1;
    private javax.swing.JLabel labelStatusV2;
    private javax.swing.JPanel panelBodyV1;
    private javax.swing.JPanel panelBodyV2;
    private javax.swing.JPanel panelHeaderBodyV1;
    private javax.swing.JPanel panelHeaderBodyV2;
    private javax.swing.JPanel panelHeaderV1;
    private javax.swing.JPanel panelHeaderV2;
    private javax.swing.JPanel panelV1;
    private javax.swing.JPanel panelV2;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JSpinner spinnerPortServerV1;
    private javax.swing.JSpinner spinnerPortServerV2;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextArea textAreaOutput;
    private javax.swing.JTextField textFieldIPServerV1;
    private javax.swing.JTextField textFieldIPServerV2;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private variables">
    private Properties interpreterProperties;
    private Properties bibleProperties;

    // Version 1
    private Socket sockV1;
    private boolean isConnectedV1;
    private SymmetricCrypter symmetricCrypterV1;

    // Version 2
    private Socket sockV2;
    private boolean isConnectedV2;
    private SymmetricCrypter symmetricCrypterV2;

    // Models
    private DefaultComboBoxModel messagesModel;
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Static variables">
    private static final String DEFAULT_IP_V1;
    private static final String DEFAULT_PORT_V1;
    private static final String DEFAULT_IP_V2;
    private static final String DEFAULT_PORT_V2;

    private static final String algorithm;
    private static final String cipherMode;
    private static final String padding;

    static
    {
        DEFAULT_IP_V1   = "127.0.0.1";
        DEFAULT_PORT_V1 = "40000";
        DEFAULT_IP_V2   = "127.0.0.1";
        DEFAULT_PORT_V2 = "40001";

        algorithm  = "DES";
        cipherMode = "ECB";
        padding    = "PKCS5Padding";
    }
    // </editor-fold>
}
