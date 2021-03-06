#ifndef __MAINWINDOW_HPP__
#define __MAINWINDOW_HPP__

#include <QWidget>
#include <QFileDialog>
//#include "ThreadServer.hpp"       // Monothread
#include "ThreadServerPool.hpp"     // Multithreads
#include "ThreadAdmin.hpp"

// Parser (default settings file)
#include "../Utils/Parser/IniParser.hpp"

namespace Ui
{
    class MainWindow;
}

class MainWindow : public QWidget
{
        Q_OBJECT

    public:

        explicit MainWindow(QWidget* parent = NULL);
        virtual ~MainWindow(void);

    private:

        void stopServer(void);

    private slots:

        // General slots
        void setWidgetsEnable(bool serverRunning);
        void showStatus(void);
        void displayMessage(const QString& msg);
        void updateClientsCount(int clientsCount);

        // Thread admin signals management
        void administratorConnected(const QString& adminLogin);
        void administratorDisconnected(void);
        void setServerSuspended(bool suspended);
        void shutdownServer(void);

        // Thread client and thread admin state management
        void threadServerStarted(void);
        void threadServerFinished(void);
        void threadAdminStarted(void);
        void threadAdminFinished(void);

        // Auto-connect private slots
        void on_pushButtonStart_clicked(void);
        void on_pushButtonStop_clicked(void);

    private:

        Ui::MainWindow *ui;
        //ThreadServer* _threadServeur;     // Monothread
        ThreadServerPool* _threadServeur;   // Multithreads
        ThreadAdmin* _threadAdmin;

        bool _threadServerStarted;
        bool _threadAdminStarted;

        bool _serverSuspended;
};

#endif /* __MAINWINDOW_HPP__ */
