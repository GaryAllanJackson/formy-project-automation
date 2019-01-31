namespace AutomationConfigurationJavaSupport
{
    partial class frmAutomationConfigurationJavaSupport
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            this.grpConfiguration = new System.Windows.Forms.GroupBox();
            this.btnMoveDown = new System.Windows.Forms.Button();
            this.btnMoveUp = new System.Windows.Forms.Button();
            this.btnRemoveTestFile = new System.Windows.Forms.Button();
            this.btnAddTestFile = new System.Windows.Forms.Button();
            this.lstTestSettingsFileName = new System.Windows.Forms.ListBox();
            this.btnSaveConfigurationSettings = new System.Windows.Forms.Button();
            this.btnBrowseTestFile = new System.Windows.Forms.Button();
            this.txtTestFileName = new System.Windows.Forms.TextBox();
            this.lblTestFileName = new System.Windows.Forms.Label();
            this.cboTestAllBrowsers = new System.Windows.Forms.ComboBox();
            this.lblTestAllBrowsers = new System.Windows.Forms.Label();
            this.btnScreenShotFolderBrowse = new System.Windows.Forms.Button();
            this.txtScreenShotFolder = new System.Windows.Forms.TextBox();
            this.lblScreenShotFolder = new System.Windows.Forms.Label();
            this.cboRunHeadless = new System.Windows.Forms.ComboBox();
            this.lblRunHeadless = new System.Windows.Forms.Label();
            this.cboBrowserType = new System.Windows.Forms.ComboBox();
            this.lblBrowserType = new System.Windows.Forms.Label();
            this.btnConfigBrowse = new System.Windows.Forms.Button();
            this.txtConfigurationFilePath = new System.Windows.Forms.TextBox();
            this.lblConfigurationFilePath = new System.Windows.Forms.Label();
            this.menuStrip1 = new System.Windows.Forms.MenuStrip();
            this.mnuFile = new System.Windows.Forms.ToolStripMenuItem();
            this.mnuFileNew = new System.Windows.Forms.ToolStripMenuItem();
            this.mnuFileNewConfigurationFile = new System.Windows.Forms.ToolStripMenuItem();
            this.mnuFileNewTestSettingsCommandFile = new System.Windows.Forms.ToolStripMenuItem();
            this.mnuFileOpen = new System.Windows.Forms.ToolStripMenuItem();
            this.mnuFileOpenConfigurationFile = new System.Windows.Forms.ToolStripMenuItem();
            this.mnuFileOpenTestSettingsCommandFile = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripMenuItem1 = new System.Windows.Forms.ToolStripSeparator();
            this.mnuFileSave = new System.Windows.Forms.ToolStripMenuItem();
            this.mnuFileSaveConfigurationFile = new System.Windows.Forms.ToolStripMenuItem();
            this.mnuFileSaveTestSettingsCommandsFile = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripMenuItem2 = new System.Windows.Forms.ToolStripSeparator();
            this.mnuFileExit = new System.Windows.Forms.ToolStripMenuItem();
            this.mnuView = new System.Windows.Forms.ToolStripMenuItem();
            this.mnuViewForm = new System.Windows.Forms.ToolStripMenuItem();
            this.mnuViewFormConfiguration = new System.Windows.Forms.ToolStripMenuItem();
            this.mnuViewFormTestSettingCommands = new System.Windows.Forms.ToolStripMenuItem();
            this.mnuTools = new System.Windows.Forms.ToolStripMenuItem();
            this.mnuToolsAdd = new System.Windows.Forms.ToolStripMenuItem();
            this.mnuToolsAddWaitDelay = new System.Windows.Forms.ToolStripMenuItem();
            this.grpTestCommands = new System.Windows.Forms.GroupBox();
            this.btnUpdateTestCommand = new System.Windows.Forms.Button();
            this.cboCrucialAssertion = new System.Windows.Forms.ComboBox();
            this.lblCrucialAssertion = new System.Windows.Forms.Label();
            this.btnGetXPath = new System.Windows.Forms.Button();
            this.lblOpenedTestFileName = new System.Windows.Forms.Label();
            this.lblOpenedTestFileNameLabel = new System.Windows.Forms.Label();
            this.lblTestCommands = new System.Windows.Forms.Label();
            this.btnRemoveCommand = new System.Windows.Forms.Button();
            this.btnAddCommand = new System.Windows.Forms.Button();
            this.cboPerformNonReadAction = new System.Windows.Forms.ComboBox();
            this.lblPerformNonReadAction = new System.Windows.Forms.Label();
            this.cboAccessorType = new System.Windows.Forms.ComboBox();
            this.lblAccessorType = new System.Windows.Forms.Label();
            this.txtExpectedValueAction = new System.Windows.Forms.TextBox();
            this.lblExpectedValueAction = new System.Windows.Forms.Label();
            this.txtAccessor = new System.Windows.Forms.TextBox();
            this.lblAccessor = new System.Windows.Forms.Label();
            this.dgvCommands = new System.Windows.Forms.DataGridView();
            this.grpWebPage = new System.Windows.Forms.GroupBox();
            this.lblURL = new System.Windows.Forms.Label();
            this.txtURL = new System.Windows.Forms.TextBox();
            this.wbTestPage = new System.Windows.Forms.WebBrowser();
            this.testCommandBindingSource = new System.Windows.Forms.BindingSource(this.components);
            this.cboSpecifyFilesOrSelectFolder = new System.Windows.Forms.ComboBox();
            this.lblFolderOfSpecificFiles = new System.Windows.Forms.Label();
            this.grpConfiguration.SuspendLayout();
            this.menuStrip1.SuspendLayout();
            this.grpTestCommands.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvCommands)).BeginInit();
            this.grpWebPage.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.testCommandBindingSource)).BeginInit();
            this.SuspendLayout();
            // 
            // grpConfiguration
            // 
            this.grpConfiguration.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.grpConfiguration.Controls.Add(this.cboSpecifyFilesOrSelectFolder);
            this.grpConfiguration.Controls.Add(this.lblFolderOfSpecificFiles);
            this.grpConfiguration.Controls.Add(this.btnMoveDown);
            this.grpConfiguration.Controls.Add(this.btnMoveUp);
            this.grpConfiguration.Controls.Add(this.btnRemoveTestFile);
            this.grpConfiguration.Controls.Add(this.btnAddTestFile);
            this.grpConfiguration.Controls.Add(this.lstTestSettingsFileName);
            this.grpConfiguration.Controls.Add(this.btnSaveConfigurationSettings);
            this.grpConfiguration.Controls.Add(this.btnBrowseTestFile);
            this.grpConfiguration.Controls.Add(this.txtTestFileName);
            this.grpConfiguration.Controls.Add(this.lblTestFileName);
            this.grpConfiguration.Controls.Add(this.cboTestAllBrowsers);
            this.grpConfiguration.Controls.Add(this.lblTestAllBrowsers);
            this.grpConfiguration.Controls.Add(this.btnScreenShotFolderBrowse);
            this.grpConfiguration.Controls.Add(this.txtScreenShotFolder);
            this.grpConfiguration.Controls.Add(this.lblScreenShotFolder);
            this.grpConfiguration.Controls.Add(this.cboRunHeadless);
            this.grpConfiguration.Controls.Add(this.lblRunHeadless);
            this.grpConfiguration.Controls.Add(this.cboBrowserType);
            this.grpConfiguration.Controls.Add(this.lblBrowserType);
            this.grpConfiguration.Controls.Add(this.btnConfigBrowse);
            this.grpConfiguration.Controls.Add(this.txtConfigurationFilePath);
            this.grpConfiguration.Controls.Add(this.lblConfigurationFilePath);
            this.grpConfiguration.Font = new System.Drawing.Font("Microsoft Sans Serif", 10.2F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.grpConfiguration.ForeColor = System.Drawing.Color.Blue;
            this.grpConfiguration.Location = new System.Drawing.Point(13, 42);
            this.grpConfiguration.Name = "grpConfiguration";
            this.grpConfiguration.Size = new System.Drawing.Size(773, 524);
            this.grpConfiguration.TabIndex = 0;
            this.grpConfiguration.TabStop = false;
            this.grpConfiguration.Text = "Configuration";
            // 
            // btnMoveDown
            // 
            this.btnMoveDown.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnMoveDown.Font = new System.Drawing.Font("Microsoft Sans Serif", 7.8F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnMoveDown.Location = new System.Drawing.Point(668, 418);
            this.btnMoveDown.Name = "btnMoveDown";
            this.btnMoveDown.Size = new System.Drawing.Size(69, 29);
            this.btnMoveDown.TabIndex = 20;
            this.btnMoveDown.Text = "Down";
            this.btnMoveDown.UseVisualStyleBackColor = true;
            this.btnMoveDown.Click += new System.EventHandler(this.btnMoveDown_Click);
            // 
            // btnMoveUp
            // 
            this.btnMoveUp.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnMoveUp.Font = new System.Drawing.Font("Microsoft Sans Serif", 7.8F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnMoveUp.Location = new System.Drawing.Point(668, 392);
            this.btnMoveUp.Name = "btnMoveUp";
            this.btnMoveUp.Size = new System.Drawing.Size(69, 26);
            this.btnMoveUp.TabIndex = 19;
            this.btnMoveUp.Text = "Up";
            this.btnMoveUp.UseVisualStyleBackColor = true;
            this.btnMoveUp.Click += new System.EventHandler(this.btnMoveUp_Click);
            // 
            // btnRemoveTestFile
            // 
            this.btnRemoveTestFile.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnRemoveTestFile.Location = new System.Drawing.Point(668, 367);
            this.btnRemoveTestFile.Name = "btnRemoveTestFile";
            this.btnRemoveTestFile.Size = new System.Drawing.Size(69, 23);
            this.btnRemoveTestFile.TabIndex = 18;
            this.btnRemoveTestFile.Text = "-";
            this.btnRemoveTestFile.UseVisualStyleBackColor = true;
            this.btnRemoveTestFile.Click += new System.EventHandler(this.btnRemoveTestFile_Click);
            // 
            // btnAddTestFile
            // 
            this.btnAddTestFile.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnAddTestFile.Location = new System.Drawing.Point(668, 343);
            this.btnAddTestFile.Name = "btnAddTestFile";
            this.btnAddTestFile.Size = new System.Drawing.Size(69, 23);
            this.btnAddTestFile.TabIndex = 17;
            this.btnAddTestFile.Text = "+";
            this.btnAddTestFile.UseVisualStyleBackColor = true;
            this.btnAddTestFile.Click += new System.EventHandler(this.btnAddTestFile_Click);
            // 
            // lstTestSettingsFileName
            // 
            this.lstTestSettingsFileName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.lstTestSettingsFileName.FormattingEnabled = true;
            this.lstTestSettingsFileName.ItemHeight = 20;
            this.lstTestSettingsFileName.Location = new System.Drawing.Point(13, 343);
            this.lstTestSettingsFileName.Name = "lstTestSettingsFileName";
            this.lstTestSettingsFileName.Size = new System.Drawing.Size(645, 104);
            this.lstTestSettingsFileName.TabIndex = 16;
            // 
            // btnSaveConfigurationSettings
            // 
            this.btnSaveConfigurationSettings.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnSaveConfigurationSettings.Location = new System.Drawing.Point(456, 454);
            this.btnSaveConfigurationSettings.Name = "btnSaveConfigurationSettings";
            this.btnSaveConfigurationSettings.Size = new System.Drawing.Size(281, 40);
            this.btnSaveConfigurationSettings.TabIndex = 15;
            this.btnSaveConfigurationSettings.Text = "Save Configuration Settings";
            this.btnSaveConfigurationSettings.UseVisualStyleBackColor = true;
            this.btnSaveConfigurationSettings.Click += new System.EventHandler(this.btnSaveConfigurationSettings_Click);
            // 
            // btnBrowseTestFile
            // 
            this.btnBrowseTestFile.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnBrowseTestFile.Location = new System.Drawing.Point(692, 309);
            this.btnBrowseTestFile.Name = "btnBrowseTestFile";
            this.btnBrowseTestFile.Size = new System.Drawing.Size(45, 23);
            this.btnBrowseTestFile.TabIndex = 14;
            this.btnBrowseTestFile.Text = "...";
            this.btnBrowseTestFile.UseVisualStyleBackColor = true;
            this.btnBrowseTestFile.Click += new System.EventHandler(this.btnBrowseTestFile_Click);
            // 
            // txtTestFileName
            // 
            this.txtTestFileName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.txtTestFileName.Location = new System.Drawing.Point(13, 305);
            this.txtTestFileName.Name = "txtTestFileName";
            this.txtTestFileName.Size = new System.Drawing.Size(648, 27);
            this.txtTestFileName.TabIndex = 13;
            // 
            // lblTestFileName
            // 
            this.lblTestFileName.AutoSize = true;
            this.lblTestFileName.Location = new System.Drawing.Point(9, 282);
            this.lblTestFileName.Name = "lblTestFileName";
            this.lblTestFileName.Size = new System.Drawing.Size(143, 20);
            this.lblTestFileName.TabIndex = 12;
            this.lblTestFileName.Text = "Test File Name:";
            // 
            // cboTestAllBrowsers
            // 
            this.cboTestAllBrowsers.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cboTestAllBrowsers.FormattingEnabled = true;
            this.cboTestAllBrowsers.Location = new System.Drawing.Point(13, 174);
            this.cboTestAllBrowsers.Name = "cboTestAllBrowsers";
            this.cboTestAllBrowsers.Size = new System.Drawing.Size(159, 28);
            this.cboTestAllBrowsers.TabIndex = 11;
            this.cboTestAllBrowsers.SelectedIndexChanged += new System.EventHandler(this.cboTestAllBrowsers_SelectedIndexChanged);
            // 
            // lblTestAllBrowsers
            // 
            this.lblTestAllBrowsers.AutoSize = true;
            this.lblTestAllBrowsers.Location = new System.Drawing.Point(6, 151);
            this.lblTestAllBrowsers.Name = "lblTestAllBrowsers";
            this.lblTestAllBrowsers.Size = new System.Drawing.Size(166, 20);
            this.lblTestAllBrowsers.TabIndex = 10;
            this.lblTestAllBrowsers.Text = "Test All Browsers:";
            // 
            // btnScreenShotFolderBrowse
            // 
            this.btnScreenShotFolderBrowse.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnScreenShotFolderBrowse.Location = new System.Drawing.Point(692, 118);
            this.btnScreenShotFolderBrowse.Name = "btnScreenShotFolderBrowse";
            this.btnScreenShotFolderBrowse.Size = new System.Drawing.Size(45, 23);
            this.btnScreenShotFolderBrowse.TabIndex = 9;
            this.btnScreenShotFolderBrowse.Text = "...";
            this.btnScreenShotFolderBrowse.UseVisualStyleBackColor = true;
            this.btnScreenShotFolderBrowse.Click += new System.EventHandler(this.btnScreenShotFolderBrowse_Click);
            // 
            // txtScreenShotFolder
            // 
            this.txtScreenShotFolder.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.txtScreenShotFolder.Location = new System.Drawing.Point(13, 114);
            this.txtScreenShotFolder.Name = "txtScreenShotFolder";
            this.txtScreenShotFolder.Size = new System.Drawing.Size(648, 27);
            this.txtScreenShotFolder.TabIndex = 8;
            // 
            // lblScreenShotFolder
            // 
            this.lblScreenShotFolder.AutoSize = true;
            this.lblScreenShotFolder.Location = new System.Drawing.Point(6, 91);
            this.lblScreenShotFolder.Name = "lblScreenShotFolder";
            this.lblScreenShotFolder.Size = new System.Drawing.Size(177, 20);
            this.lblScreenShotFolder.TabIndex = 7;
            this.lblScreenShotFolder.Text = "Screen Shot Folder:";
            // 
            // cboRunHeadless
            // 
            this.cboRunHeadless.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cboRunHeadless.FormattingEnabled = true;
            this.cboRunHeadless.Location = new System.Drawing.Point(410, 176);
            this.cboRunHeadless.Name = "cboRunHeadless";
            this.cboRunHeadless.Size = new System.Drawing.Size(156, 28);
            this.cboRunHeadless.TabIndex = 6;
            this.cboRunHeadless.SelectedIndexChanged += new System.EventHandler(this.cboRunHeadless_SelectedIndexChanged);
            // 
            // lblRunHeadless
            // 
            this.lblRunHeadless.AutoSize = true;
            this.lblRunHeadless.Location = new System.Drawing.Point(406, 153);
            this.lblRunHeadless.Name = "lblRunHeadless";
            this.lblRunHeadless.Size = new System.Drawing.Size(133, 20);
            this.lblRunHeadless.TabIndex = 5;
            this.lblRunHeadless.Text = "Run Headless:";
            // 
            // cboBrowserType
            // 
            this.cboBrowserType.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cboBrowserType.FormattingEnabled = true;
            this.cboBrowserType.Location = new System.Drawing.Point(207, 176);
            this.cboBrowserType.Name = "cboBrowserType";
            this.cboBrowserType.Size = new System.Drawing.Size(156, 28);
            this.cboBrowserType.TabIndex = 4;
            this.cboBrowserType.SelectedIndexChanged += new System.EventHandler(this.cboBrowserType_SelectedIndexChanged);
            // 
            // lblBrowserType
            // 
            this.lblBrowserType.AutoSize = true;
            this.lblBrowserType.Location = new System.Drawing.Point(212, 151);
            this.lblBrowserType.Name = "lblBrowserType";
            this.lblBrowserType.Size = new System.Drawing.Size(131, 20);
            this.lblBrowserType.TabIndex = 3;
            this.lblBrowserType.Text = "Browser Type:";
            // 
            // btnConfigBrowse
            // 
            this.btnConfigBrowse.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnConfigBrowse.Location = new System.Drawing.Point(692, 53);
            this.btnConfigBrowse.Name = "btnConfigBrowse";
            this.btnConfigBrowse.Size = new System.Drawing.Size(45, 23);
            this.btnConfigBrowse.TabIndex = 2;
            this.btnConfigBrowse.Text = "...";
            this.btnConfigBrowse.UseVisualStyleBackColor = true;
            this.btnConfigBrowse.Click += new System.EventHandler(this.btnConfigBrowse_Click);
            // 
            // txtConfigurationFilePath
            // 
            this.txtConfigurationFilePath.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.txtConfigurationFilePath.Location = new System.Drawing.Point(13, 53);
            this.txtConfigurationFilePath.Name = "txtConfigurationFilePath";
            this.txtConfigurationFilePath.Size = new System.Drawing.Size(648, 27);
            this.txtConfigurationFilePath.TabIndex = 1;
            // 
            // lblConfigurationFilePath
            // 
            this.lblConfigurationFilePath.AutoSize = true;
            this.lblConfigurationFilePath.Location = new System.Drawing.Point(6, 30);
            this.lblConfigurationFilePath.Name = "lblConfigurationFilePath";
            this.lblConfigurationFilePath.Size = new System.Drawing.Size(208, 20);
            this.lblConfigurationFilePath.TabIndex = 0;
            this.lblConfigurationFilePath.Text = "Configuration File Path:";
            // 
            // menuStrip1
            // 
            this.menuStrip1.ImageScalingSize = new System.Drawing.Size(20, 20);
            this.menuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.mnuFile,
            this.mnuView,
            this.mnuTools});
            this.menuStrip1.Location = new System.Drawing.Point(0, 0);
            this.menuStrip1.Name = "menuStrip1";
            this.menuStrip1.Size = new System.Drawing.Size(798, 28);
            this.menuStrip1.TabIndex = 1;
            this.menuStrip1.Text = "menuStrip1";
            // 
            // mnuFile
            // 
            this.mnuFile.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.mnuFileNew,
            this.mnuFileOpen,
            this.toolStripMenuItem1,
            this.mnuFileSave,
            this.toolStripMenuItem2,
            this.mnuFileExit});
            this.mnuFile.Name = "mnuFile";
            this.mnuFile.Size = new System.Drawing.Size(44, 24);
            this.mnuFile.Text = "&File";
            // 
            // mnuFileNew
            // 
            this.mnuFileNew.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.mnuFileNewConfigurationFile,
            this.mnuFileNewTestSettingsCommandFile});
            this.mnuFileNew.Name = "mnuFileNew";
            this.mnuFileNew.Size = new System.Drawing.Size(120, 26);
            this.mnuFileNew.Text = "&New";
            // 
            // mnuFileNewConfigurationFile
            // 
            this.mnuFileNewConfigurationFile.Name = "mnuFileNewConfigurationFile";
            this.mnuFileNewConfigurationFile.Size = new System.Drawing.Size(268, 26);
            this.mnuFileNewConfigurationFile.Text = "&Configuration File";
            this.mnuFileNewConfigurationFile.Click += new System.EventHandler(this.mnuFileNewConfigurationFile_Click);
            // 
            // mnuFileNewTestSettingsCommandFile
            // 
            this.mnuFileNewTestSettingsCommandFile.Name = "mnuFileNewTestSettingsCommandFile";
            this.mnuFileNewTestSettingsCommandFile.Size = new System.Drawing.Size(268, 26);
            this.mnuFileNewTestSettingsCommandFile.Text = "&Test Settings Command File";
            this.mnuFileNewTestSettingsCommandFile.Click += new System.EventHandler(this.mnuFileNewTestSettingsCommandFile_Click);
            // 
            // mnuFileOpen
            // 
            this.mnuFileOpen.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.mnuFileOpenConfigurationFile,
            this.mnuFileOpenTestSettingsCommandFile});
            this.mnuFileOpen.Name = "mnuFileOpen";
            this.mnuFileOpen.Size = new System.Drawing.Size(120, 26);
            this.mnuFileOpen.Text = "&Open";
            // 
            // mnuFileOpenConfigurationFile
            // 
            this.mnuFileOpenConfigurationFile.Name = "mnuFileOpenConfigurationFile";
            this.mnuFileOpenConfigurationFile.Size = new System.Drawing.Size(268, 26);
            this.mnuFileOpenConfigurationFile.Text = "&Configuration File";
            this.mnuFileOpenConfigurationFile.Click += new System.EventHandler(this.mnuFileOpenConfigurationFile_Click);
            // 
            // mnuFileOpenTestSettingsCommandFile
            // 
            this.mnuFileOpenTestSettingsCommandFile.Name = "mnuFileOpenTestSettingsCommandFile";
            this.mnuFileOpenTestSettingsCommandFile.Size = new System.Drawing.Size(268, 26);
            this.mnuFileOpenTestSettingsCommandFile.Text = "&Test Settings Command File";
            this.mnuFileOpenTestSettingsCommandFile.Click += new System.EventHandler(this.mnuFileOpenTestSettingsCommandFile_Click);
            // 
            // toolStripMenuItem1
            // 
            this.toolStripMenuItem1.Name = "toolStripMenuItem1";
            this.toolStripMenuItem1.Size = new System.Drawing.Size(117, 6);
            // 
            // mnuFileSave
            // 
            this.mnuFileSave.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.mnuFileSaveConfigurationFile,
            this.mnuFileSaveTestSettingsCommandsFile});
            this.mnuFileSave.Name = "mnuFileSave";
            this.mnuFileSave.Size = new System.Drawing.Size(120, 26);
            this.mnuFileSave.Text = "&Save";
            // 
            // mnuFileSaveConfigurationFile
            // 
            this.mnuFileSaveConfigurationFile.Name = "mnuFileSaveConfigurationFile";
            this.mnuFileSaveConfigurationFile.Size = new System.Drawing.Size(274, 26);
            this.mnuFileSaveConfigurationFile.Text = "&Configuration File";
            this.mnuFileSaveConfigurationFile.Click += new System.EventHandler(this.mnuFileSaveConfigurationFile_Click);
            // 
            // mnuFileSaveTestSettingsCommandsFile
            // 
            this.mnuFileSaveTestSettingsCommandsFile.Name = "mnuFileSaveTestSettingsCommandsFile";
            this.mnuFileSaveTestSettingsCommandsFile.Size = new System.Drawing.Size(274, 26);
            this.mnuFileSaveTestSettingsCommandsFile.Text = "&Test Settings Commands File";
            this.mnuFileSaveTestSettingsCommandsFile.Click += new System.EventHandler(this.mnuFileSaveTestSettingsCommandsFile_Click);
            // 
            // toolStripMenuItem2
            // 
            this.toolStripMenuItem2.Name = "toolStripMenuItem2";
            this.toolStripMenuItem2.Size = new System.Drawing.Size(117, 6);
            // 
            // mnuFileExit
            // 
            this.mnuFileExit.Name = "mnuFileExit";
            this.mnuFileExit.Size = new System.Drawing.Size(120, 26);
            this.mnuFileExit.Text = "E&xit";
            this.mnuFileExit.Click += new System.EventHandler(this.mnuFileExit_Click);
            // 
            // mnuView
            // 
            this.mnuView.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.mnuViewForm});
            this.mnuView.Name = "mnuView";
            this.mnuView.Size = new System.Drawing.Size(53, 24);
            this.mnuView.Text = "&View";
            // 
            // mnuViewForm
            // 
            this.mnuViewForm.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.mnuViewFormConfiguration,
            this.mnuViewFormTestSettingCommands});
            this.mnuViewForm.Name = "mnuViewForm";
            this.mnuViewForm.Size = new System.Drawing.Size(118, 26);
            this.mnuViewForm.Text = "&Form";
            // 
            // mnuViewFormConfiguration
            // 
            this.mnuViewFormConfiguration.Name = "mnuViewFormConfiguration";
            this.mnuViewFormConfiguration.Size = new System.Drawing.Size(241, 26);
            this.mnuViewFormConfiguration.Text = "Configuration";
            this.mnuViewFormConfiguration.Click += new System.EventHandler(this.mnuViewFormConfiguration_Click);
            // 
            // mnuViewFormTestSettingCommands
            // 
            this.mnuViewFormTestSettingCommands.Name = "mnuViewFormTestSettingCommands";
            this.mnuViewFormTestSettingCommands.Size = new System.Drawing.Size(241, 26);
            this.mnuViewFormTestSettingCommands.Text = "Test Setting Commands";
            this.mnuViewFormTestSettingCommands.Click += new System.EventHandler(this.mnuViewFormTestSettingCommands_Click);
            // 
            // mnuTools
            // 
            this.mnuTools.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.mnuToolsAdd});
            this.mnuTools.Name = "mnuTools";
            this.mnuTools.Size = new System.Drawing.Size(57, 24);
            this.mnuTools.Text = "Tools";
            // 
            // mnuToolsAdd
            // 
            this.mnuToolsAdd.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.mnuToolsAddWaitDelay});
            this.mnuToolsAdd.Name = "mnuToolsAdd";
            this.mnuToolsAdd.Size = new System.Drawing.Size(112, 26);
            this.mnuToolsAdd.Text = "&Add";
            // 
            // mnuToolsAddWaitDelay
            // 
            this.mnuToolsAddWaitDelay.Name = "mnuToolsAddWaitDelay";
            this.mnuToolsAddWaitDelay.Size = new System.Drawing.Size(246, 26);
            this.mnuToolsAddWaitDelay.Text = "&Wait / Delay (5 seconds)";
            this.mnuToolsAddWaitDelay.Click += new System.EventHandler(this.mnuToolsAddWaitDelay_Click);
            // 
            // grpTestCommands
            // 
            this.grpTestCommands.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.grpTestCommands.Controls.Add(this.btnUpdateTestCommand);
            this.grpTestCommands.Controls.Add(this.cboCrucialAssertion);
            this.grpTestCommands.Controls.Add(this.lblCrucialAssertion);
            this.grpTestCommands.Controls.Add(this.btnGetXPath);
            this.grpTestCommands.Controls.Add(this.lblOpenedTestFileName);
            this.grpTestCommands.Controls.Add(this.lblOpenedTestFileNameLabel);
            this.grpTestCommands.Controls.Add(this.lblTestCommands);
            this.grpTestCommands.Controls.Add(this.btnRemoveCommand);
            this.grpTestCommands.Controls.Add(this.btnAddCommand);
            this.grpTestCommands.Controls.Add(this.cboPerformNonReadAction);
            this.grpTestCommands.Controls.Add(this.lblPerformNonReadAction);
            this.grpTestCommands.Controls.Add(this.cboAccessorType);
            this.grpTestCommands.Controls.Add(this.lblAccessorType);
            this.grpTestCommands.Controls.Add(this.txtExpectedValueAction);
            this.grpTestCommands.Controls.Add(this.lblExpectedValueAction);
            this.grpTestCommands.Controls.Add(this.txtAccessor);
            this.grpTestCommands.Controls.Add(this.lblAccessor);
            this.grpTestCommands.Controls.Add(this.dgvCommands);
            this.grpTestCommands.Font = new System.Drawing.Font("Microsoft Sans Serif", 10.2F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.grpTestCommands.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(0)))), ((int)(((byte)(192)))), ((int)(((byte)(0)))));
            this.grpTestCommands.Location = new System.Drawing.Point(13, 43);
            this.grpTestCommands.Name = "grpTestCommands";
            this.grpTestCommands.Size = new System.Drawing.Size(774, 502);
            this.grpTestCommands.TabIndex = 2;
            this.grpTestCommands.TabStop = false;
            this.grpTestCommands.Text = "Test Commands";
            this.grpTestCommands.Visible = false;
            // 
            // btnUpdateTestCommand
            // 
            this.btnUpdateTestCommand.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnUpdateTestCommand.Font = new System.Drawing.Font("Microsoft Sans Serif", 9F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnUpdateTestCommand.Location = new System.Drawing.Point(481, 256);
            this.btnUpdateTestCommand.Name = "btnUpdateTestCommand";
            this.btnUpdateTestCommand.Size = new System.Drawing.Size(104, 28);
            this.btnUpdateTestCommand.TabIndex = 17;
            this.btnUpdateTestCommand.Text = "Update";
            this.btnUpdateTestCommand.UseVisualStyleBackColor = true;
            this.btnUpdateTestCommand.Click += new System.EventHandler(this.btnUpdateTestCommand_Click);
            // 
            // cboCrucialAssertion
            // 
            this.cboCrucialAssertion.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cboCrucialAssertion.FormattingEnabled = true;
            this.cboCrucialAssertion.Location = new System.Drawing.Point(511, 164);
            this.cboCrucialAssertion.Name = "cboCrucialAssertion";
            this.cboCrucialAssertion.Size = new System.Drawing.Size(204, 28);
            this.cboCrucialAssertion.TabIndex = 16;
            // 
            // lblCrucialAssertion
            // 
            this.lblCrucialAssertion.AutoSize = true;
            this.lblCrucialAssertion.Location = new System.Drawing.Point(509, 141);
            this.lblCrucialAssertion.Name = "lblCrucialAssertion";
            this.lblCrucialAssertion.Size = new System.Drawing.Size(161, 20);
            this.lblCrucialAssertion.TabIndex = 15;
            this.lblCrucialAssertion.Text = "Crucial Assertion:";
            // 
            // btnGetXPath
            // 
            this.btnGetXPath.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnGetXPath.Font = new System.Drawing.Font("Microsoft Sans Serif", 10.2F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnGetXPath.Location = new System.Drawing.Point(692, 52);
            this.btnGetXPath.Name = "btnGetXPath";
            this.btnGetXPath.Size = new System.Drawing.Size(58, 28);
            this.btnGetXPath.TabIndex = 14;
            this.btnGetXPath.Text = "Get";
            this.btnGetXPath.UseVisualStyleBackColor = true;
            this.btnGetXPath.Click += new System.EventHandler(this.btnGetXPath_Click);
            // 
            // lblOpenedTestFileName
            // 
            this.lblOpenedTestFileName.AutoSize = true;
            this.lblOpenedTestFileName.ForeColor = System.Drawing.Color.Blue;
            this.lblOpenedTestFileName.Location = new System.Drawing.Point(35, 225);
            this.lblOpenedTestFileName.Name = "lblOpenedTestFileName";
            this.lblOpenedTestFileName.Size = new System.Drawing.Size(100, 20);
            this.lblOpenedTestFileName.TabIndex = 13;
            this.lblOpenedTestFileName.Text = "File Name:";
            this.lblOpenedTestFileName.Visible = false;
            // 
            // lblOpenedTestFileNameLabel
            // 
            this.lblOpenedTestFileNameLabel.AutoSize = true;
            this.lblOpenedTestFileNameLabel.Location = new System.Drawing.Point(35, 205);
            this.lblOpenedTestFileNameLabel.Name = "lblOpenedTestFileNameLabel";
            this.lblOpenedTestFileNameLabel.Size = new System.Drawing.Size(100, 20);
            this.lblOpenedTestFileNameLabel.TabIndex = 12;
            this.lblOpenedTestFileNameLabel.Text = "File Name:";
            this.lblOpenedTestFileNameLabel.Visible = false;
            // 
            // lblTestCommands
            // 
            this.lblTestCommands.AutoSize = true;
            this.lblTestCommands.Location = new System.Drawing.Point(26, 264);
            this.lblTestCommands.Name = "lblTestCommands";
            this.lblTestCommands.Size = new System.Drawing.Size(151, 20);
            this.lblTestCommands.TabIndex = 11;
            this.lblTestCommands.Text = "Test Commands:";
            // 
            // btnRemoveCommand
            // 
            this.btnRemoveCommand.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnRemoveCommand.Font = new System.Drawing.Font("Microsoft Sans Serif", 13.8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnRemoveCommand.Location = new System.Drawing.Point(603, 257);
            this.btnRemoveCommand.Name = "btnRemoveCommand";
            this.btnRemoveCommand.Size = new System.Drawing.Size(58, 28);
            this.btnRemoveCommand.TabIndex = 10;
            this.btnRemoveCommand.Text = "-";
            this.btnRemoveCommand.UseVisualStyleBackColor = true;
            this.btnRemoveCommand.Click += new System.EventHandler(this.btnRemoveCommand_Click);
            // 
            // btnAddCommand
            // 
            this.btnAddCommand.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnAddCommand.Font = new System.Drawing.Font("Microsoft Sans Serif", 13.8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnAddCommand.Location = new System.Drawing.Point(668, 257);
            this.btnAddCommand.Name = "btnAddCommand";
            this.btnAddCommand.Size = new System.Drawing.Size(58, 28);
            this.btnAddCommand.TabIndex = 9;
            this.btnAddCommand.Text = "+";
            this.btnAddCommand.UseVisualStyleBackColor = true;
            this.btnAddCommand.Click += new System.EventHandler(this.btnAddCommand_Click);
            // 
            // cboPerformNonReadAction
            // 
            this.cboPerformNonReadAction.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cboPerformNonReadAction.FormattingEnabled = true;
            this.cboPerformNonReadAction.Location = new System.Drawing.Point(269, 164);
            this.cboPerformNonReadAction.Name = "cboPerformNonReadAction";
            this.cboPerformNonReadAction.Size = new System.Drawing.Size(204, 28);
            this.cboPerformNonReadAction.TabIndex = 8;
            // 
            // lblPerformNonReadAction
            // 
            this.lblPerformNonReadAction.AutoSize = true;
            this.lblPerformNonReadAction.Location = new System.Drawing.Point(267, 141);
            this.lblPerformNonReadAction.Name = "lblPerformNonReadAction";
            this.lblPerformNonReadAction.Size = new System.Drawing.Size(230, 20);
            this.lblPerformNonReadAction.TabIndex = 7;
            this.lblPerformNonReadAction.Text = "Perform Non-Read Action:";
            // 
            // cboAccessorType
            // 
            this.cboAccessorType.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cboAccessorType.FormattingEnabled = true;
            this.cboAccessorType.Location = new System.Drawing.Point(27, 164);
            this.cboAccessorType.Name = "cboAccessorType";
            this.cboAccessorType.Size = new System.Drawing.Size(219, 28);
            this.cboAccessorType.TabIndex = 6;
            // 
            // lblAccessorType
            // 
            this.lblAccessorType.AutoSize = true;
            this.lblAccessorType.Location = new System.Drawing.Point(23, 141);
            this.lblAccessorType.Name = "lblAccessorType";
            this.lblAccessorType.Size = new System.Drawing.Size(140, 20);
            this.lblAccessorType.TabIndex = 5;
            this.lblAccessorType.Text = "Accessor Type:";
            // 
            // txtExpectedValueAction
            // 
            this.txtExpectedValueAction.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.txtExpectedValueAction.Location = new System.Drawing.Point(27, 106);
            this.txtExpectedValueAction.Name = "txtExpectedValueAction";
            this.txtExpectedValueAction.Size = new System.Drawing.Size(687, 27);
            this.txtExpectedValueAction.TabIndex = 4;
            // 
            // lblExpectedValueAction
            // 
            this.lblExpectedValueAction.AutoSize = true;
            this.lblExpectedValueAction.Location = new System.Drawing.Point(23, 83);
            this.lblExpectedValueAction.Name = "lblExpectedValueAction";
            this.lblExpectedValueAction.Size = new System.Drawing.Size(204, 20);
            this.lblExpectedValueAction.TabIndex = 3;
            this.lblExpectedValueAction.Text = "Expected Value/Action:";
            // 
            // txtAccessor
            // 
            this.txtAccessor.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.txtAccessor.Location = new System.Drawing.Point(27, 50);
            this.txtAccessor.Name = "txtAccessor";
            this.txtAccessor.Size = new System.Drawing.Size(647, 27);
            this.txtAccessor.TabIndex = 2;
            // 
            // lblAccessor
            // 
            this.lblAccessor.AutoSize = true;
            this.lblAccessor.Location = new System.Drawing.Point(23, 29);
            this.lblAccessor.Name = "lblAccessor";
            this.lblAccessor.Size = new System.Drawing.Size(94, 20);
            this.lblAccessor.TabIndex = 1;
            this.lblAccessor.Text = "Accessor:";
            // 
            // dgvCommands
            // 
            this.dgvCommands.AllowUserToAddRows = false;
            this.dgvCommands.AllowUserToDeleteRows = false;
            this.dgvCommands.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.dgvCommands.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.dgvCommands.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgvCommands.Location = new System.Drawing.Point(19, 294);
            this.dgvCommands.MultiSelect = false;
            this.dgvCommands.Name = "dgvCommands";
            this.dgvCommands.RowTemplate.DefaultCellStyle.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleLeft;
            this.dgvCommands.RowTemplate.DefaultCellStyle.NullValue = null;
            this.dgvCommands.RowTemplate.Height = 24;
            this.dgvCommands.RowTemplate.Resizable = System.Windows.Forms.DataGridViewTriState.True;
            this.dgvCommands.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.CellSelect;
            this.dgvCommands.Size = new System.Drawing.Size(730, 188);
            this.dgvCommands.TabIndex = 0;
            this.dgvCommands.CellContentClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvCommands_CellContentClick);
            // 
            // grpWebPage
            // 
            this.grpWebPage.Controls.Add(this.lblURL);
            this.grpWebPage.Controls.Add(this.txtURL);
            this.grpWebPage.Controls.Add(this.wbTestPage);
            this.grpWebPage.Font = new System.Drawing.Font("Microsoft Sans Serif", 10.2F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.grpWebPage.ForeColor = System.Drawing.Color.Maroon;
            this.grpWebPage.Location = new System.Drawing.Point(2, 43);
            this.grpWebPage.Name = "grpWebPage";
            this.grpWebPage.Size = new System.Drawing.Size(785, 486);
            this.grpWebPage.TabIndex = 14;
            this.grpWebPage.TabStop = false;
            this.grpWebPage.Text = "Web Page:";
            this.grpWebPage.Visible = false;
            // 
            // lblURL
            // 
            this.lblURL.AutoSize = true;
            this.lblURL.Location = new System.Drawing.Point(24, 30);
            this.lblURL.Name = "lblURL";
            this.lblURL.Size = new System.Drawing.Size(52, 20);
            this.lblURL.TabIndex = 2;
            this.lblURL.Text = "URL:";
            // 
            // txtURL
            // 
            this.txtURL.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.txtURL.Location = new System.Drawing.Point(89, 27);
            this.txtURL.Name = "txtURL";
            this.txtURL.Size = new System.Drawing.Size(648, 27);
            this.txtURL.TabIndex = 1;
            this.txtURL.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.txtURL_KeyPress);
            // 
            // wbTestPage
            // 
            this.wbTestPage.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.wbTestPage.Location = new System.Drawing.Point(24, 64);
            this.wbTestPage.MinimumSize = new System.Drawing.Size(20, 20);
            this.wbTestPage.Name = "wbTestPage";
            this.wbTestPage.Size = new System.Drawing.Size(736, 400);
            this.wbTestPage.TabIndex = 0;
            this.wbTestPage.DocumentCompleted += new System.Windows.Forms.WebBrowserDocumentCompletedEventHandler(this.wbTestPage_DocumentCompleted);
            // 
            // testCommandBindingSource
            // 
            this.testCommandBindingSource.DataSource = typeof(AutomationConfigurationJavaSupport.Entities.TestCommand);
            // 
            // cboSpecifyFilesOrSelectFolder
            // 
            this.cboSpecifyFilesOrSelectFolder.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.cboSpecifyFilesOrSelectFolder.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cboSpecifyFilesOrSelectFolder.FormattingEnabled = true;
            this.cboSpecifyFilesOrSelectFolder.Location = new System.Drawing.Point(13, 245);
            this.cboSpecifyFilesOrSelectFolder.Name = "cboSpecifyFilesOrSelectFolder";
            this.cboSpecifyFilesOrSelectFolder.Size = new System.Drawing.Size(649, 28);
            this.cboSpecifyFilesOrSelectFolder.TabIndex = 22;
            this.cboSpecifyFilesOrSelectFolder.SelectedIndexChanged += new System.EventHandler(this.cboSpecifyFilesOrSelectFolder_SelectedIndexChanged);
            // 
            // lblFolderOfSpecificFiles
            // 
            this.lblFolderOfSpecificFiles.AutoSize = true;
            this.lblFolderOfSpecificFiles.Location = new System.Drawing.Point(13, 222);
            this.lblFolderOfSpecificFiles.Name = "lblFolderOfSpecificFiles";
            this.lblFolderOfSpecificFiles.Size = new System.Drawing.Size(418, 20);
            this.lblFolderOfSpecificFiles.TabIndex = 21;
            this.lblFolderOfSpecificFiles.Text = "Specificify Test Script files (..or select a folder):";
            // 
            // frmAutomationConfigurationJavaSupport
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(798, 549);
            this.Controls.Add(this.grpConfiguration);
            this.Controls.Add(this.grpTestCommands);
            this.Controls.Add(this.menuStrip1);
            this.Controls.Add(this.grpWebPage);
            this.Name = "frmAutomationConfigurationJavaSupport";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "Java Automation Configuration Support Utility";
            this.Load += new System.EventHandler(this.frmAutomationConfigurationJavaSupport_Load);
            this.grpConfiguration.ResumeLayout(false);
            this.grpConfiguration.PerformLayout();
            this.menuStrip1.ResumeLayout(false);
            this.menuStrip1.PerformLayout();
            this.grpTestCommands.ResumeLayout(false);
            this.grpTestCommands.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvCommands)).EndInit();
            this.grpWebPage.ResumeLayout(false);
            this.grpWebPage.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.testCommandBindingSource)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.GroupBox grpConfiguration;
        private System.Windows.Forms.ComboBox cboBrowserType;
        private System.Windows.Forms.Label lblBrowserType;
        private System.Windows.Forms.Button btnConfigBrowse;
        private System.Windows.Forms.TextBox txtConfigurationFilePath;
        private System.Windows.Forms.Label lblConfigurationFilePath;
        private System.Windows.Forms.Button btnScreenShotFolderBrowse;
        private System.Windows.Forms.TextBox txtScreenShotFolder;
        private System.Windows.Forms.Label lblScreenShotFolder;
        private System.Windows.Forms.ComboBox cboRunHeadless;
        private System.Windows.Forms.Label lblRunHeadless;
        private System.Windows.Forms.Button btnBrowseTestFile;
        private System.Windows.Forms.TextBox txtTestFileName;
        private System.Windows.Forms.Label lblTestFileName;
        private System.Windows.Forms.ComboBox cboTestAllBrowsers;
        private System.Windows.Forms.Label lblTestAllBrowsers;
        private System.Windows.Forms.Button btnSaveConfigurationSettings;
        private System.Windows.Forms.MenuStrip menuStrip1;
        private System.Windows.Forms.ToolStripMenuItem mnuFile;
        private System.Windows.Forms.ToolStripMenuItem mnuFileSave;
        private System.Windows.Forms.ToolStripMenuItem mnuFileSaveConfigurationFile;
        private System.Windows.Forms.ToolStripMenuItem mnuFileSaveTestSettingsCommandsFile;
        private System.Windows.Forms.ToolStripMenuItem mnuFileOpen;
        private System.Windows.Forms.ToolStripMenuItem mnuFileOpenConfigurationFile;
        private System.Windows.Forms.ToolStripMenuItem mnuFileOpenTestSettingsCommandFile;
        private System.Windows.Forms.ToolStripSeparator toolStripMenuItem1;
        private System.Windows.Forms.ToolStripSeparator toolStripMenuItem2;
        private System.Windows.Forms.ToolStripMenuItem mnuFileExit;
        private System.Windows.Forms.GroupBox grpTestCommands;
        private System.Windows.Forms.ToolStripMenuItem mnuView;
        private System.Windows.Forms.ToolStripMenuItem mnuViewForm;
        private System.Windows.Forms.ToolStripMenuItem mnuViewFormConfiguration;
        private System.Windows.Forms.ToolStripMenuItem mnuViewFormTestSettingCommands;
        private System.Windows.Forms.Label lblTestCommands;
        private System.Windows.Forms.Button btnRemoveCommand;
        private System.Windows.Forms.Button btnAddCommand;
        private System.Windows.Forms.ComboBox cboPerformNonReadAction;
        private System.Windows.Forms.Label lblPerformNonReadAction;
        private System.Windows.Forms.ComboBox cboAccessorType;
        private System.Windows.Forms.Label lblAccessorType;
        private System.Windows.Forms.TextBox txtExpectedValueAction;
        private System.Windows.Forms.Label lblExpectedValueAction;
        private System.Windows.Forms.TextBox txtAccessor;
        private System.Windows.Forms.Label lblAccessor;
        private System.Windows.Forms.DataGridView dgvCommands;
        private System.Windows.Forms.BindingSource testCommandBindingSource;
        private System.Windows.Forms.ToolStripMenuItem mnuFileNew;
        private System.Windows.Forms.ToolStripMenuItem mnuFileNewConfigurationFile;
        private System.Windows.Forms.ToolStripMenuItem mnuFileNewTestSettingsCommandFile;
        private System.Windows.Forms.Label lblOpenedTestFileName;
        private System.Windows.Forms.Label lblOpenedTestFileNameLabel;
        private System.Windows.Forms.GroupBox grpWebPage;
        private System.Windows.Forms.Label lblURL;
        private System.Windows.Forms.TextBox txtURL;
        private System.Windows.Forms.WebBrowser wbTestPage;
        private System.Windows.Forms.Button btnGetXPath;
        private System.Windows.Forms.ComboBox cboCrucialAssertion;
        private System.Windows.Forms.Label lblCrucialAssertion;
        private System.Windows.Forms.Button btnRemoveTestFile;
        private System.Windows.Forms.Button btnAddTestFile;
        private System.Windows.Forms.ListBox lstTestSettingsFileName;
        private System.Windows.Forms.Button btnMoveDown;
        private System.Windows.Forms.Button btnMoveUp;
        private System.Windows.Forms.Button btnUpdateTestCommand;
        private System.Windows.Forms.ToolStripMenuItem mnuTools;
        private System.Windows.Forms.ToolStripMenuItem mnuToolsAdd;
        private System.Windows.Forms.ToolStripMenuItem mnuToolsAddWaitDelay;
        private System.Windows.Forms.ComboBox cboSpecifyFilesOrSelectFolder;
        private System.Windows.Forms.Label lblFolderOfSpecificFiles;
    }
}

