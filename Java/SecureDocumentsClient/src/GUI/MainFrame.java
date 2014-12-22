package GUI;

import MyLittleCheapLibrary.CIAManager;
import SPF.Authentication.Authentication;
import SPF.Cle;
import SPF.Crypto.Chiffrement;
import SPF.Integrity.Integrity;
import Utils.BytesConverter;
import Utils.MessageBoxes;
import Utils.PropertyLoader;
import Utils.Request;
import Utils.TextAreaOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author nakim
 */
public class MainFrame extends javax.swing.JFrame
{
    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public MainFrame()
    {
        this.initComponents();

        // Redirect the system output to a TextArea
        TextAreaOutputStream toas =
            TextAreaOutputStream.getInstance(this.textAreaOutput);

        this.sock = null;

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

            this.prop = PropertyLoader.load(path);

            // Set the default ip server
            this.textFieldIPServer.setText(
                this.prop.getProperty("ip_server", DEFAULT_IP));

            // Set the default port server
            this.spinnerPortServer.setValue(
                new Integer(this.prop.getProperty("port_server", DEFAULT_PORT)));

            System.out.println("[ OK ] Parametres de configuration charges");
        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }

        // Creates models
        this.createModels();

        // Hide tab
        this.disconnectFromServer();

        // Center frame
        this.setLocationRelativeTo(null);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Private methods">
    private void createModels()
    {
        this.chiffrementProvidersModel = new DefaultComboBoxModel(
            new String[] {"AlbertiFamily", "Triumvirat", "ProCrypto", "CryptoCBCAESProvider"});

        this.AuthenticationProvidersModels = new DefaultComboBoxModel(
            new String[]{"HMACSHA1MawetProvider"});

        this.IntegrityProvidersModel = new DefaultComboBoxModel(
            new String[]{"SHA1MawetProvider"});

        // Application des modèles aux widgets
        this.comboBoxCipherProviders.setModel(this.chiffrementProvidersModel);
        this.comboBoxAuthenticationProviders.setModel(this.AuthenticationProvidersModels);
        this.comboBoxIntegrityProviders.setModel(this.IntegrityProvidersModel);
    }

    private void getCleFromFile(String keyname)
    {
        try
        {
            final FileInputStream fis = new FileInputStream(keysFolderPath + keyname);
            ObjectInputStream ois = new ObjectInputStream(fis);

            try
            {
                this.cle = (Cle) ois.readObject();
            }
            finally
            {
                // on ferme les flux
                try
                {
                    ois.close();
                }
                finally
                {
                    fis.close();
                }
            }
        }
        catch (IOException | ClassNotFoundException ex)
        {
            System.err.println(ex);
            this.cle = null;
        }
    }

    private void connectToServer()
    {
        if (this.sock != null)
            this.disconnectFromServer();

        int port = (int)this.spinnerPortServer.getValue();
        String ip = this.textFieldIPServer.getText();
        System.out.println("[ OK ] Tentative de connexion au serveur " +
                           ip + ":" + port);

        try
        {
            this.sock = new Socket(ip, port);
        }
        catch (UnknownHostException ex)
        {
            System.out.println("[FAIL] Hote introuvable. L'IP " + ip + " est invalide");
            return;
        }
        catch (IOException ex)
        {
            System.out.println("[FAIL] Impossible de se connecter");
            return;
        }

        System.out.println("[ OK ] Connexion etablie");

        this.buttonConnect.setText("Déconnexion");
        this.isConnected = true;
    }

    private void disconnectFromServer()
    {
        this.buttonConnect.setText("Conexion");
        this.isConnected = false;

        if (this.sock == null)
        {
            System.out.println("[ OK ] Vous n'etes pas connecte au serveur");
            return;
        }

        try
        {
            this.sock.close();
            this.sock = null;
        }
        catch (IOException ex)
        {
            System.out.println("[FAIL] Une erreur est survenue lors de la deconnexion : " + ex);
        }
        finally
        {
            System.out.println("[ OK  ] Vous etes deconnecte du serveur");
        }
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        panelHeader = new javax.swing.JPanel();
        labelIPServer = new javax.swing.JLabel();
        textFieldIPServer = new javax.swing.JTextField();
        labelPortServer = new javax.swing.JLabel();
        spinnerPortServer = new javax.swing.JSpinner();
        buttonConnect = new javax.swing.JButton();
        buttonClear = new javax.swing.JButton();
        splitPane = new javax.swing.JSplitPane();
        tabbedPane = new javax.swing.JTabbedPane();
        panelGetDocuments = new javax.swing.JPanel();
        labelDocumentName = new javax.swing.JLabel();
        textFieldDocumentName = new javax.swing.JTextField();
        checkBoxEncrypt = new javax.swing.JCheckBox();
        labelCipherProvider = new javax.swing.JLabel();
        comboBoxCipherProviders = new javax.swing.JComboBox();
        labelCipherKey = new javax.swing.JLabel();
        textFieldCipherKeyName = new javax.swing.JTextField();
        checkBoxAuthentication = new javax.swing.JCheckBox();
        labelAuthenticationProvider = new javax.swing.JLabel();
        comboBoxAuthenticationProviders = new javax.swing.JComboBox();
        labelAuthenticationKey = new javax.swing.JLabel();
        textFieldAuthenticationKeyName = new javax.swing.JTextField();
        checkBoxIntegrity = new javax.swing.JCheckBox();
        labelIntegrityProvider = new javax.swing.JLabel();
        comboBoxIntegrityProviders = new javax.swing.JComboBox();
        buttonGetDocument = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        textAreaOutput = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Secure Document Client");

        labelIPServer.setText("IP Address : ");
        panelHeader.add(labelIPServer);

        textFieldIPServer.setPreferredSize(new java.awt.Dimension(100, 28));
        panelHeader.add(textFieldIPServer);

        labelPortServer.setText("Port :");
        panelHeader.add(labelPortServer);

        spinnerPortServer.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        spinnerPortServer.setPreferredSize(new java.awt.Dimension(100, 28));
        panelHeader.add(spinnerPortServer);

        buttonConnect.setText("Connect");
        buttonConnect.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonConnectActionPerformed(evt);
            }
        });
        panelHeader.add(buttonConnect);

        getContentPane().add(panelHeader, java.awt.BorderLayout.PAGE_START);

        buttonClear.setText("Clear");
        buttonClear.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonClearActionPerformed(evt);
            }
        });
        getContentPane().add(buttonClear, java.awt.BorderLayout.PAGE_END);

        splitPane.setDividerLocation(300);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        panelGetDocuments.setLayout(new java.awt.GridBagLayout());

        labelDocumentName.setText("Document name : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(labelDocumentName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.4;
        panelGetDocuments.add(textFieldDocumentName, gridBagConstraints);

        checkBoxEncrypt.setText("Encrypt file");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(checkBoxEncrypt, gridBagConstraints);

        labelCipherProvider.setText("Cipher provider : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(labelCipherProvider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(comboBoxCipherProviders, gridBagConstraints);

        labelCipherKey.setText("Cipher key : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(labelCipherKey, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(textFieldCipherKeyName, gridBagConstraints);

        checkBoxAuthentication.setText("Authenticate file");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(checkBoxAuthentication, gridBagConstraints);

        labelAuthenticationProvider.setText("Authentication provider : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(labelAuthenticationProvider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(comboBoxAuthenticationProviders, gridBagConstraints);

        labelAuthenticationKey.setText("Authentication key : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(labelAuthenticationKey, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(textFieldAuthenticationKeyName, gridBagConstraints);

        checkBoxIntegrity.setText("Check integrity");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        panelGetDocuments.add(checkBoxIntegrity, gridBagConstraints);

        labelIntegrityProvider.setText("Integrity provider : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(labelIntegrityProvider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(comboBoxIntegrityProviders, gridBagConstraints);

        buttonGetDocument.setText("Get document");
        buttonGetDocument.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                buttonGetDocumentActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelGetDocuments.add(buttonGetDocument, gridBagConstraints);

        tabbedPane.addTab("Get Documents", panelGetDocuments);

        splitPane.setTopComponent(tabbedPane);

        scrollPane.setViewportView(textAreaOutput);

        splitPane.setRightComponent(scrollPane);

        getContentPane().add(splitPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //<editor-fold defaultstate="collapsed" desc="Events management">
    private void buttonConnectActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonConnectActionPerformed
    {//GEN-HEADEREND:event_buttonConnectActionPerformed
        if (this.isConnected)
            this.disconnectFromServer();
        else
            this.connectToServer();
    }//GEN-LAST:event_buttonConnectActionPerformed

    private void buttonGetDocumentActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonGetDocumentActionPerformed
    {//GEN-HEADEREND:event_buttonGetDocumentActionPerformed
        try
        {
            if (this.sock == null || !this.sock.isConnected())
                throw new Exception("Vous n'etes pas connecte au serveur");

            if (this.textFieldDocumentName.getText().isEmpty())
                throw new Exception("Vous devez renseigner un nom de fichier");

            if (this.checkBoxEncrypt.isSelected() &&
                this.textFieldCipherKeyName.getText().isEmpty())
                throw new Exception("Vous devez renseigner le nom de la cle utilisee pour chiffrer");

            if (this.checkBoxAuthentication.isSelected() &&
                this.textFieldAuthenticationKeyName.getText().isEmpty())
                throw new Exception("Vous devez renseigner le nom de la cle utilisee pour authentifier");
        }
        catch (Exception e)
        {
            System.out.println("[FAIL] " + e.getMessage());
            MessageBoxes.ShowError(e.getMessage(), "Données manquantes");
            return;
        }

        System.out.println("[ OK ] Construction de la requete...");
        Request reply, requ = new Request("GET_DOCUMENT");

        // Ajout du nom du fichier
        requ.addArg(this.textFieldDocumentName.getText());

        // Ajout des flags des paramètres
        requ.addArg(BytesConverter.toByteArray(
            this.checkBoxEncrypt.isSelected()));
        requ.addArg(BytesConverter.toByteArray(
            this.checkBoxAuthentication.isSelected()));
        requ.addArg(BytesConverter.toByteArray(
            this.checkBoxIntegrity.isSelected()));

        // Ajout du provider de chiffrement et du nom de la clé
        if (this.checkBoxEncrypt.isSelected())
        {
            requ.addArg((String)this.comboBoxCipherProviders.getSelectedItem());
            requ.addArg(this.textFieldCipherKeyName.getText());
        }

        // Ajout du provider de Authentification et du nom de la clé
        if (this.checkBoxAuthentication.isSelected())
        {
            requ.addArg((String)this.comboBoxAuthenticationProviders.getSelectedItem());
            requ.addArg(this.textFieldAuthenticationKeyName.getText());
        }

        // Ajout du provider Integrity
        if (this.checkBoxIntegrity.isSelected())
        {
            requ.addArg((String)this.comboBoxIntegrityProviders.getSelectedItem());
        }

        reply = requ.sendAndRecv(this.sock);

        if (reply.getCommand().compareToIgnoreCase("GET_DOCUMENT_ACK") == 0)
        {
            int currentIndex = 0;
            String content = reply.getStringArg(currentIndex);
            System.out.println("[ RQ ] Texte recu : " + content);

            // décryptage du text
            if (this.checkBoxEncrypt.isSelected())
            {
                this.chiffrement = CIAManager.getChiffrement(
                    (String)this.comboBoxCipherProviders.getSelectedItem());
                this.getCleFromFile(this.textFieldCipherKeyName.getText());
                this.chiffrement.init(this.cle);

                content = this.chiffrement.decrypte(content);
                System.out.println("[ RQ ] Texte decrypte : " + content);
            }

            // Vérification de l'authentification
            if (this.checkBoxAuthentication.isSelected())
            {
                byte[] hmac = reply.getArg(++currentIndex);
                this.authentication = CIAManager.getAuthentication(
                    (String)this.comboBoxAuthenticationProviders.getSelectedItem());
                this.getCleFromFile(this.textFieldAuthenticationKeyName.getText());
                this.authentication.init(this.cle);

                if (this.authentication.verifyAuthenticate(content, hmac))
                    System.out.println("[ RQ ] Authentification valide");
                else
                    System.out.println("[FAIL] Authentification non valide");
            }

            if (this.checkBoxIntegrity.isSelected())
            {
                byte[] hash = reply.getArg(++currentIndex);
                this.integrity = CIAManager.getIntegrity(
                    (String)this.comboBoxIntegrityProviders.getSelectedItem());

                if (this.integrity.verifyCheck(content, hash))
                    System.out.println("[ RQ ] Integrite valide");
                else
                    System.out.println("[FAIL] integrite non valide");
            }
        }
        else if (reply.getCommand().equalsIgnoreCase("GET_DOCUMENT_FAIL"))
        {
            String cause = reply.getStringArg(0);
            System.out.println("[FAIL] " + cause);
            MessageBoxes.ShowError(cause, "Erreur de requête");
        }
    }//GEN-LAST:event_buttonGetDocumentActionPerformed

    private void buttonClearActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buttonClearActionPerformed
    {//GEN-HEADEREND:event_buttonClearActionPerformed
        this.textAreaOutput.setText(null);
    }//GEN-LAST:event_buttonClearActionPerformed
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Widgets">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClear;
    private javax.swing.JButton buttonConnect;
    private javax.swing.JButton buttonGetDocument;
    private javax.swing.JCheckBox checkBoxAuthentication;
    private javax.swing.JCheckBox checkBoxEncrypt;
    private javax.swing.JCheckBox checkBoxIntegrity;
    private javax.swing.JComboBox comboBoxAuthenticationProviders;
    private javax.swing.JComboBox comboBoxCipherProviders;
    private javax.swing.JComboBox comboBoxIntegrityProviders;
    private javax.swing.JLabel labelAuthenticationKey;
    private javax.swing.JLabel labelAuthenticationProvider;
    private javax.swing.JLabel labelCipherKey;
    private javax.swing.JLabel labelCipherProvider;
    private javax.swing.JLabel labelDocumentName;
    private javax.swing.JLabel labelIPServer;
    private javax.swing.JLabel labelIntegrityProvider;
    private javax.swing.JLabel labelPortServer;
    private javax.swing.JPanel panelGetDocuments;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JSpinner spinnerPortServer;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextArea textAreaOutput;
    private javax.swing.JTextField textFieldAuthenticationKeyName;
    private javax.swing.JTextField textFieldCipherKeyName;
    private javax.swing.JTextField textFieldDocumentName;
    private javax.swing.JTextField textFieldIPServer;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Private variables">
    private Properties prop;
    private Socket sock;
    private boolean isConnected;

    // Models
    private ComboBoxModel chiffrementProvidersModel;
    private ComboBoxModel IntegrityProvidersModel;
    private ComboBoxModel AuthenticationProvidersModels;

    private Cle cle;
    private Chiffrement chiffrement;
    private Integrity integrity;
    private Authentication authentication;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Static variables">
    private static final String DEFAULT_IP;
    private static final String DEFAULT_PORT;
    private static final String keysFolderPath;

    static
    {
        DEFAULT_IP   = "127.0.0.1";
        DEFAULT_PORT = "40000";
        keysFolderPath = "KEYS" + System.getProperty("file.separator");
    }
    // </editor-fold>
}
