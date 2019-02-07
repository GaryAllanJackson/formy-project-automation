using AutomationConfigurationJavaSupport.Entities;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace AutomationConfigurationJavaSupport
{
    public partial class frmAutomationConfigurationJavaSupport : Form
    {
        public static string ConfigurationFileName { get; set; }    //= "ConfigurationSetup.tconfig"; //"testSetup.config";
        //List<string> fileComments = new List<string>();
        string[] comments;
        //make this a binding list so that the clicking the grid doesn't throw IndexOutOfRangeException
        BindingList<TestCommand> testCommands;
        string[] testCommandComments;
        bool elementClicked = false;
        int selectedRow;

        public frmAutomationConfigurationJavaSupport()
        {
            InitializeComponent();
        }

        private void frmAutomationConfigurationJavaSupport_Load(object sender, EventArgs e)
        {
            SetConfigurationFileName();
            //configuration specific items
            LoadTrueFalse(cboRunHeadless);
            LoadTrueFalse(cboTestAllBrowsers);
            LoadBrowsers(cboBrowserType);
            LoadTrueFalse(cboSpecifyFilesOrSelectFolder);
            LoadFileFilterType(cboFileFilterType);
            //LoadFolderOrSpecificFiles(cboFolderOfSpecificFiles);
            PopulateFileComments();
            PopulateTestCommandComments();
           

            ConfigureGroupBoxes();

            //Test Command specific items
            InitializeGrid();
            LoadTrueFalse(cboPerformNonReadAction);
            LoadAccessorTypes(cboAccessorType);
            LoadTrueFalse(cboCrucialAssertion);
        }

        private void LoadFileFilterType(ComboBox cboBx)
        {
            cboBx.Items.Add("Starts With");
            cboBx.Items.Add("Contains");
            cboBx.Items.Add("Ends With");
        }

        //private void LoadFolderOrSpecificFiles(ComboBox cboFolderOfSpecificFiles)
        //{

        //}

        private void SetConfigurationFileName()
        {
            ConfigurationFileName = ConfigurationManager.AppSettings["ConfigurationFileName"] != null ? ConfigurationManager.AppSettings["ConfigurationFileName"].ToString() : "ConfigurationSetup.tconfig";
        }

        private void dgvCommands_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            try
            {
                if (e.RowIndex > -1 && e.ColumnIndex > -1)
                {
                    selectedRow = e.RowIndex;
                    DataGridViewCell cell = dgvCommands[e.ColumnIndex, e.RowIndex];
                    int row = dgvCommands.CurrentCell.RowIndex;
                    //DataGridViewCell cell = dgvCommands.SelectedCells[0];
                    txtAccessor.Text = dgvCommands[0, row].Value.ToString();
                    txtExpectedValueAction.Text = dgvCommands[1, row].Value.ToString();
                    cboAccessorType.SelectedIndex  = cboAccessorType.FindString(dgvCommands[2, row].Value.ToString());
                    cboPerformNonReadAction.SelectedIndex = cboPerformNonReadAction.FindString(dgvCommands[3, row].Value.ToString());
                    cboCrucialAssertion.SelectedIndex = cboCrucialAssertion.FindString(dgvCommands[4, row].Value.ToString());
                }
            }
            catch(Exception ex)
            {
                MessageBox.Show("Error clicking cell contents" + ex.Message, "Grid Error!!!");
            }
        }

        private void cboSpecifyFilesOrSelectFolder_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (cboSpecifyFilesOrSelectFolder.SelectedIndex == 1)
            {
                lblTestFileName.Text = "Test Folder Name:";
                EnableSelectedFiles(false);
                lstTestSettingsFileName.Enabled = false;
                txtFolderFilter.Enabled = true;
                cboFileFilterType.Enabled = true;
            }
            else if (cboSpecifyFilesOrSelectFolder.SelectedIndex == 0)
            {                
                lblTestFileName.Text = "Test File Name:";
                EnableSelectedFiles(true);
                lstTestSettingsFileName.Enabled = true;
                txtFolderFilter.Enabled = false;
                //txtFolderFilter.Text =
                cboFileFilterType.Enabled = false;
            }
        }

        private void EnableSelectedFiles(bool tf)
        {
            btnAddTestFile.Enabled = tf;
            btnRemoveTestFile.Enabled = tf;
            btnMoveUp.Enabled = tf;
            btnMoveDown.Enabled = tf;            
        }

        #region { Configuration Button Controls }
        private void btnConfigBrowse_Click(object sender, EventArgs e)
        {
            SelectFolder(txtConfigurationFilePath);
        }

        private void btnScreenShotFolderBrowse_Click(object sender, EventArgs e)
        {
            SelectFolder(txtScreenShotFolder);
        }

        private void btnBrowseTestFile_Click(object sender, EventArgs e)
        {
            if (cboSpecifyFilesOrSelectFolder.SelectedIndex == 0)
            {
                SelectFile(txtTestFileName);
            }
            else
            {
                SelectFolder(txtTestFileName);
            }            
        }

        private void btnSaveConfigurationSettings_Click(object sender, EventArgs e)
        {
            SaveConfigurationFile();
        }        

        private void btnAddTestFile_Click(object sender, EventArgs e)
        {
            if (!string.IsNullOrEmpty(txtTestFileName.Text))
            {
                lstTestSettingsFileName.Items.Add(txtTestFileName.Text);
            }
        }

        private void btnRemoveTestFile_Click(object sender, EventArgs e)
        {
            if (lstTestSettingsFileName.SelectedIndex > -1)
            {
                lstTestSettingsFileName.Items.RemoveAt(lstTestSettingsFileName.SelectedIndex);
            }
        }
        #endregion

        #region {Browser Buttons}
        private void btnGetXPath_Click(object sender, EventArgs e)
        {
            grpWebPage.Visible = true;
            grpWebPage.BringToFront();
            elementClicked = false;
        }
        #endregion

        #region { Combo Box Components }
        private void cboRunHeadless_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (cboRunHeadless.SelectedIndex == 0 && cboTestAllBrowsers.SelectedIndex == 0)
            {
                ShowHeadlessMessage();
            }
        }

        private void cboTestAllBrowsers_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (cboTestAllBrowsers.SelectedIndex == 0)
            {
                cboBrowserType.SelectedIndex = -1;
                cboBrowserType.Enabled = false;
                if (cboRunHeadless.SelectedIndex == 0)
                {
                    ShowHeadlessMessage();
                }
            }
            else
            {
                cboBrowserType.Enabled = true;
            }
        }

        private void cboBrowserType_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (cboBrowserType.SelectedIndex == 2)
            {
                cboRunHeadless.SelectedIndex = 0;
                cboRunHeadless.Enabled = false;
            }
            else
            {
                cboRunHeadless.Enabled = true;
            }
        }
        #endregion

        #region { Menu Controls }
        private void mnuFileExit_Click(object sender, EventArgs e)
        {
            Dispose();
        }

        private void mnuFileSaveConfigurationFile_Click(object sender, EventArgs e)
        {
            SaveConfigurationFile();
        }

        private void mnuFileSaveTestSettingsCommandsFile_Click(object sender, EventArgs e)
        {
            SaveTestSettingCommandsFile();
            ToggleOpenFileState(false);
        }

        private void mnuViewFormConfiguration_Click(object sender, EventArgs e)
        {
            grpConfiguration.Visible = true;
            grpConfiguration.BringToFront();
            grpTestCommands.Visible = false;
            ShowGroupBox(grpConfiguration);
        }

        private void mnuViewFormTestSettingCommands_Click(object sender, EventArgs e)
        {
            //grpTestCommands.Visible = true;
            //grpTestCommands.BringToFront();
            //grpConfiguration.Visible = false;
            ShowGroupBox(grpTestCommands);
        }

        private void mnuFileOpenConfigurationFile_Click(object sender, EventArgs e)
        {
            mnuViewFormConfiguration_Click(sender, e);
            ReadConfigurationFile();
        }

       


        private void mnuFileOpenTestSettingsCommandFile_Click(object sender, EventArgs e)
        {
            mnuViewFormTestSettingCommands_Click(sender, e);
            ReadTestSettingsFile();
        }

        private void mnuFileNewConfigurationFile_Click(object sender, EventArgs e)
        {
            txtConfigurationFilePath.Text = string.Empty;
            txtTestFileName.Text = string.Empty;
            txtScreenShotFolder.Text = string.Empty;
            cboRunHeadless.SelectedIndex = -1;
            cboBrowserType.SelectedIndex = -1;
            cboTestAllBrowsers.SelectedIndex = -1;
            cboSpecifyFilesOrSelectFolder.SelectedIndex = -1;
            cboFileFilterType.SelectedIndex = -1;
            txtFolderFilter.Text = string.Empty;
            lstTestSettingsFileName.Items.Clear();
            btnAddTestFile.Enabled = true;
            btnRemoveTestFile.Enabled = true;
            btnMoveUp.Enabled = true;
            btnMoveDown.Enabled = true;
        }

        private void mnuFileNewTestSettingsCommandFile_Click(object sender, EventArgs e)
        {
            //this will reinitialize the testCommands too!
            InitializeGrid();
            ToggleOpenFileState(false);
            txtAccessor.Text = string.Empty;
            txtExpectedValueAction.Text = string.Empty;
            cboAccessorType.SelectedIndex = -1;
            cboPerformNonReadAction.SelectedIndex = -1;
            cboCrucialAssertion.SelectedIndex = -1;
        }

        #endregion


        #region { Test Settings Command Buttons }
        private void btnAddCommand_Click(object sender, EventArgs e)
        {
            if (!string.IsNullOrEmpty(txtAccessor.Text) && !string.IsNullOrEmpty(txtExpectedValueAction.Text) &&
                cboAccessorType.SelectedIndex > -1 && cboPerformNonReadAction.SelectedIndex > -1)
            {
                TestCommand item = new TestCommand
                {
                    Accessor = txtAccessor.Text,
                    ExpectedValueAction = txtExpectedValueAction.Text,
                    AccessorType = cboAccessorType.SelectedItem.ToString(),
                    IsNonReadAction = cboPerformNonReadAction.SelectedItem.ToString(),
                    IsCrucial = cboCrucialAssertion.SelectedItem.ToString()
                };
                testCommands.Add(item);

                UpdateDataGrid();
                //dgvCommands.DataSource = new List<TestCommand>();
                //dgvCommands.Refresh();
                //if (testCommands.Count > 0)
                //{
                //    dgvCommands.DataSource = testCommands;
                //    dgvCommands.Update();
                //    dgvCommands.AutoResizeColumns();
                //}
            }
        }

        private void btnRemoveCommand_Click(object sender, EventArgs e)
        {
            try
            {
                int row = dgvCommands.CurrentCell.RowIndex;
                //DataGridViewCell cell = dgvCommands.SelectedCells[0];
                string accessor = dgvCommands[0, row].Value.ToString();
                string expected = dgvCommands[1, row].Value.ToString();
                string accessorType = dgvCommands[2, row].Value.ToString();
                string isAction = dgvCommands[3, row].Value.ToString();
                string isCrucial = dgvCommands[4, row].Value.ToString();

                
                TestCommand item = testCommands[row];
                if (item.Accessor == accessor && item.ExpectedValueAction == expected && 
                    item.AccessorType == accessorType && item.IsNonReadAction == isAction &&
                    item.IsCrucial == isCrucial)
                {
                    //you have the correct item, nothing more to do
                }
                else
                {
                    //just in case the indexes are off, which should never happen
                    item = (from t in testCommands
                            where t.Accessor.Equals(accessor) &&
                                 t.AccessorType.Equals(accessorType) &&
                                 t.ExpectedValueAction.Equals(expected) &&
                                 t.IsNonReadAction.Equals(isAction) &&
                                 t.IsCrucial.Equals(isCrucial)
                            select t).FirstOrDefault();
                }

                testCommands.Remove(item);
                UpdateDataGrid();
                //dgvCommands.DataSource = null;
                //dgvCommands.DataSource = testCommands;
                //dgvCommands.Refresh();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error:" + ex.Message, "Unusual Error with Grid");
            }
        }

        private void txtURL_KeyPress(object sender, KeyPressEventArgs e)
        {
            if (e.KeyChar == (char)13)
            {
                wbTestPage.Navigate(txtURL.Text);
            }
        }

        private void wbTestPage_DocumentCompleted(object sender, WebBrowserDocumentCompletedEventArgs e)
        {
            //add this handler after navigation
            this.wbTestPage.Document.Body.MouseDown += new HtmlElementEventHandler(Body_MouseDown);
        }

        void Body_MouseDown(Object sender, HtmlElementEventArgs e)
        {
            if (!elementClicked)
            {
                switch (e.MouseButtonsPressed)
                {
                    case MouseButtons.Left:
                        HtmlElement element = this.wbTestPage.Document.GetElementFromPoint(e.ClientMousePosition);
                        //if (element != null && "submit".Equals(element.GetAttribute("type"), StringComparison.OrdinalIgnoreCase))
                        if (element != null)
                        {
                            GetElementXPath(e);
                        }
                        break;
                }
            }
        }

        private void btnMoveUp_Click(object sender, EventArgs e)
        {
            string prevItem;
            int selectedIndex;

            if (lstTestSettingsFileName.SelectedIndex > 0)
            {
                selectedIndex = lstTestSettingsFileName.SelectedIndex;
                prevItem = lstTestSettingsFileName.Items[lstTestSettingsFileName.SelectedIndex - 1].ToString();
                lstTestSettingsFileName.Items.RemoveAt(lstTestSettingsFileName.SelectedIndex - 1);
                lstTestSettingsFileName.Items.Insert(selectedIndex, prevItem);
            }
        }

        private void btnMoveDown_Click(object sender, EventArgs e)
        {
            string item;
            int selectedIndex;

            if (lstTestSettingsFileName.SelectedIndex > -1 && lstTestSettingsFileName.SelectedIndex < lstTestSettingsFileName.Items.Count - 1)
            {
                selectedIndex = lstTestSettingsFileName.SelectedIndex;
                item = lstTestSettingsFileName.SelectedItem.ToString();
                lstTestSettingsFileName.Items.RemoveAt(lstTestSettingsFileName.SelectedIndex);
                lstTestSettingsFileName.Items.Insert(selectedIndex + 1, item);
                lstTestSettingsFileName.SelectedIndex = selectedIndex + 1;
            }
        }

        private void btnUpdateTestCommand_Click(object sender, EventArgs e)
        {
            if (selectedRow > 0)
            {
                if (!string.IsNullOrEmpty(txtAccessor.Text) && !string.IsNullOrEmpty(txtExpectedValueAction.Text) &&
                cboAccessorType.SelectedIndex > -1 && cboPerformNonReadAction.SelectedIndex > -1 && cboCrucialAssertion.SelectedIndex > -1)
                {
                    TestCommand item = testCommands[selectedRow];
                    item.Accessor = txtAccessor.Text;
                    item.ExpectedValueAction = txtExpectedValueAction.Text;
                    item.AccessorType = cboAccessorType.SelectedItem.ToString();
                    item.IsCrucial = cboCrucialAssertion.SelectedItem.ToString();
                    item.IsNonReadAction = cboPerformNonReadAction.SelectedItem.ToString();
                    testCommands[selectedRow] = item;
                    UpdateDataGrid();
                }
            }
        }
        #endregion


        #region { Helper Methods }

        private void ShowGroupBox(GroupBox grpBx)
        {
            grpBx.Visible = true;
            grpBx.BringToFront();
            if (grpBx == grpConfiguration)
            {
                grpTestCommands.Visible = false;
            }
            else
            {
                grpConfiguration.Visible = false;
            }
        }

        private void LoadTrueFalse(ComboBox cboBx)
        {
            cboBx.Items.Add("true");
            cboBx.Items.Add("false");
        }

        private void LoadBrowsers(ComboBox cboBx)
        {
            cboBx.Items.Add("Chrome");
            cboBx.Items.Add("Firefox");
            cboBx.Items.Add("PhantomJs");
        }

        private void LoadAccessorTypes(ComboBox cboBx)
        {
            cboBx.Items.Add("xPath");
            cboBx.Items.Add("CssSelector");
            cboBx.Items.Add("TagName");
            cboBx.Items.Add("ClassName");
            cboBx.Items.Add("ID");
            cboBx.Items.Add("n/a");
        }


        private void SelectFolder(TextBox txtBx)
        {
            FolderBrowserDialog fld = new FolderBrowserDialog();
            if (!string.IsNullOrEmpty(txtBx.Text) && Directory.Exists(txtBx.Text))
            {
                fld.SelectedPath = txtBx.Text;
            }
            DialogResult rslt = fld.ShowDialog();
            if (rslt == DialogResult.OK && !string.IsNullOrEmpty(fld.SelectedPath))
            {
                txtBx.Text = fld.SelectedPath;
            }
        }

        private void SelectFile(TextBox txtBx)
        {
            OpenFileDialog cdFile = new OpenFileDialog();
            cdFile.Filter = "Text Files|*.txt|All Files|*.*";

            DialogResult rslt = cdFile.ShowDialog();
            if (rslt == DialogResult.OK && !string.IsNullOrEmpty(cdFile.FileName))
            {
                txtBx.Text = cdFile.FileName;
            }
        }

        private void ShowHeadlessMessage()
        {
            MessageBox.Show("PhantomJs can only be run in headless mode, but the other two browsers can run in both modes.", "Information", MessageBoxButtons.OK, MessageBoxIcon.Information);
        }

        private void SaveConfigurationFile()
        {
            StringBuilder sb = new StringBuilder();
            if (!string.IsNullOrEmpty(txtConfigurationFilePath.Text) && !string.IsNullOrEmpty(txtScreenShotFolder.Text) && 
                ((lstTestSettingsFileName.Items.Count > 0 && cboSpecifyFilesOrSelectFolder.SelectedIndex == 0) 
                || !string.IsNullOrEmpty(txtTestFileName.Text) && cboSpecifyFilesOrSelectFolder.SelectedIndex == 1) 
                && (cboBrowserType.SelectedIndex > -1 || cboBrowserType.Enabled == false) && 
                (cboRunHeadless.SelectedIndex > -1 || cboRunHeadless.Enabled == false) && cboTestAllBrowsers.SelectedIndex > -1)
            {
               
                foreach (string item in comments)
                {
                    sb.AppendLine(item);
                }
                for (int x = 0; x < lstTestSettingsFileName.Items.Count; x++)
                {
                    sb.AppendLine(string.Format("TestFileName{0}={1}", x.ToString(), lstTestSettingsFileName.Items[x].ToString()));
                }
                sb.AppendLine(string.Format("ScreenShotSaveFolder={0}", txtScreenShotFolder.Text));
                sb.AppendLine(string.Format("BrowserType={0}", cboBrowserType.SelectedItem.ToString()));
                sb.AppendLine(string.Format("RunHeadless={0}", cboRunHeadless.SelectedItem.ToString()));
                sb.AppendLine(string.Format("TestAllBrowsers={0}", cboTestAllBrowsers.SelectedItem.ToString()));
                sb.AppendLine(string.Format("SpecifyTestFiles={0}", cboSpecifyFilesOrSelectFolder.SelectedItem.ToString()));
                sb.AppendLine(string.Format("TestFolderName={0}", txtTestFileName.Text));
                sb.AppendLine(string.Format("FolderFileFilterType={0}", cboFileFilterType.SelectedItem.ToString().Replace(" ","_")));
                sb.AppendLine(string.Format("FolderFileFilter={0}", txtFolderFilter.Text));

                SaveFile(Path.Combine(txtConfigurationFilePath.Text, ConfigurationFileName), sb.ToString());
            }
        }

        private void SaveFile(string fileName, string fileContents)
        {
            if (File.Exists(fileName))
            {
                DialogResult rslt = MessageBox.Show("The file already exists, select yes to rename the existing file and select no to overwrite the existing file.", "File Already Exists!!!", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Warning);
                if (rslt == DialogResult.Yes)
                {
                    string newFileName = RenameExistingFile(fileName);
                    if (!string.IsNullOrEmpty(newFileName))
                    {
                        MessageBox.Show("The existing file has been renamed to: " + newFileName, "File Renamed");
                    }
                }
            }

            using (StreamWriter sw = new StreamWriter(fileName))
            {
                sw.Write(fileContents);
            }
        }

        private string RenameExistingFile(string fileName)
        {
            int fCounter = 0;
            string newFileName = fileName + ".bak";

            if (File.Exists(newFileName))
            {
                while (File.Exists(newFileName))
                {
                    newFileName = fileName + fCounter.ToString() + ".bak";
                    if (!File.Exists(newFileName))
                    {
                        File.Move(fileName, newFileName);
                        break;
                    }
                    fCounter++;
                }
            }
            else
            {
                File.Move(fileName, newFileName);
            }
            return newFileName;
        }

        private void PopulateFileComments()
        {
            //"//All, will cycle through all browsers",
            //"//TestPageRoot - site page to be tested",
            comments = new String[]{ "// NOTES: Lines beginning with double slashes denote comments and will be ignored by the configuration reader.",
                                    "// TestFileName - names beginning with this are used to point to the file/files containing the test setting commands.",
                                    "//    -   The Test Setting Commands file is a colon delimited file with the following structure:",
                                    "//    -   [URL/XPath/CssSelector/TagName] ; [Action/Expected value] ; [Element Lookup Type] ; [Perform Action other than Read Value] ; [Critical Assertion]",
                                    "// ScreenShotSaveFolder - folder where screenshots should be saved - Must already exist",
                                    "// BrowserType values: Firefox, Chrome, PhantomJS",
                                    "// RunHeadless - can be true to run headless or false to show the browser, but PhantomJs is always headless",
                                    "// TestAllBrowsers - can be true or false.  If false, BrowserType must be set.  If true, BrowserType is ignored and the program will cycle through all browsers.",
                                    "// SpecifyTestFiles - Can be true to specifiy each file and the order that files are run, or false to select a folder of files that will be ordered alphabetically.",
                                    "// TestFolderName - will contain the folder where test files exist when SpecifyTestFiles is false.",
                                    "// FolderFileFilterType - type of filtering you want to use to select similarly named files within a folder options are: ",
                                    "//    -   [Starts With], [Contains] and [Ends With] ",
                                    "//    -   [Starts With] - will select only the test files starting with the filter entered",
                                    "//    -   [Contains] - will select only test files containing the filter entered",
                                    "//    -   [Ends With] - will select only test files ending with the filter entered",
                                    "// FolderFileFilter - the filter used to select only matching files within the Test Folder."
                                    };
        }

        private void PopulateTestCommandComments()
        {
            testCommandComments = new string[] {
                "### Comments in this file use 3 pound signs and anything following these will be ignored by the test program.",
                "### Add URL to navigate to, first URL is where browser navigates, second url is what the expected URL will be, in case of redirection etc...",
                "### Each parameter is separate by a semi-colon to prevent interference with navigation urls and colons",
                "### The first parameter url to navigate to, xPath, CssSelector, Tag Name",
                "### The second parameter action to take or expected value to retrieve for URLs both are required separated by a dash, ",
                "###     -   optionally add a second dash delimiter to add a time delay (thread sleep value in milliseconds) to give the event time to complete.",
                "### The third parameter is the type of check to perform and will be ignored for performing Navigation where that is irrelevant",
                "### The fourth parameter is the PeformAction boolean and true when text should be entered, a click occurs or Navigating and false when reading element values",
                "### The fifth parameter is the IsCrucial boolean.  When true, if the assertion fails the test stops immediately.  When false, if the assertion fails, the tests continue.",
                "### --------------------------------|-------------------------|-----------------------|-----------------------------------------------------",
                "### [URL/XPath/CssSelector/TagName] ; [Action/Expected value] ; [Element Lookup Type] ; [Perform Action other than Read Value] ; [Critical Assertion]"
            };
        }

        private void InitializeGrid()
        {
            testCommands = new BindingList<TestCommand>();
            UpdateDataGrid();
            //dgvCommands.DataSource = null;

            //dgvCommands.DataSource = testCommands;
            //dgvCommands.Update();
        }

        private void ConfigureGroupBoxes()
        {
            try
            {
                //foreach (GroupBox grp in this.Controls)
                foreach (Control grp in this.Controls)
                {
                    //if (grp is GroupBox)
                    if (grp.GetType() == typeof(GroupBox))
                    {
                        ConfigureGroupBoxes(grp);
                    }
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("The following error occurred:" + ex.Message, "Error Configuring Group Boxes!");
            }
        }

        private void ConfigureGroupBoxes(Control grp)
        {
            int margin = 15;
            int top = mnuFile.Height + 13;
            int left = 10;
            int width = this.Width - (margin + (2 * left));
            int height = this.Height - (margin + (2 * top));
            grp.Top = top;
            grp.Left = left;
            grp.Width = width;
            grp.Height = height;
            grp.Parent = this;
            grp.Anchor = (AnchorStyles.Left | AnchorStyles.Right | AnchorStyles.Top | AnchorStyles.Bottom);
        }

        private void SaveTestSettingCommandsFile()
        {
            StringBuilder sb = new StringBuilder();
            if (dgvCommands.RowCount > 1)
            {
                if (dgvCommands.DataSource == testCommands)
                {
                    foreach (string item in testCommandComments)
                    {
                        sb.AppendLine(item);
                    }

                    foreach (TestCommand item in testCommands)
                    {
                        sb.AppendLine(string.Format("{0} ; {1} ; {2} ; {3} ; {4}", item.Accessor, item.ExpectedValueAction, item.AccessorType, item.IsNonReadAction, item.IsCrucial));
                    }
                    SaveTestCommandFile(sb.ToString());
                }
            }
            else
            {
                MessageBox.Show("There are no commands to save!", "Nothing to save!!!");
            }
        }

        private void SaveTestCommandFile(string fileContents)
        {
            SaveFileDialog cdFile = new SaveFileDialog();
            cdFile.Filter = "Text Files|*.txt|All Files|*.*";
            cdFile.Title = "Save Test Commands File";
            if (!string.IsNullOrEmpty(lblOpenedTestFileName.Text))
            {
                cdFile.FileName = lblOpenedTestFileName.Text;
            }

            DialogResult rslt = cdFile.ShowDialog();
            if (rslt == DialogResult.OK && !string.IsNullOrEmpty(cdFile.FileName))
            {
                SaveFile(cdFile.FileName, fileContents);
            }
        }


        private void ReadTestSettingsFile()
        {
            string line;
            string[] lineItems;
            string filter = "Text Files|*.txt|All Files|*.*";
            string dialogTitle = "Open Configuration File";
            string fileName = SelectFile(filter, dialogTitle);
            if (!string.IsNullOrEmpty(fileName))
            {
                if (!string.IsNullOrEmpty(fileName))
                {
                    txtConfigurationFilePath.Text = fileName.Substring(0, fileName.LastIndexOf("\\"));
                    InitializeGrid();
                    using (StreamReader sr = new StreamReader(fileName))
                    {
                        while (!sr.EndOfStream)
                        {
                            line = sr.ReadLine();
                            if (!string.IsNullOrEmpty(line) && !line.StartsWith("###"))
                            {
                                lineItems = line.Split(';');
                                try
                                {
                                    TestCommand item = new TestCommand
                                    {
                                        Accessor = !string.IsNullOrEmpty(lineItems[0]) ? lineItems[0].Trim() : string.Empty,
                                        ExpectedValueAction = !string.IsNullOrEmpty(lineItems[1]) ? lineItems[1].Trim() : string.Empty,
                                        AccessorType = !string.IsNullOrEmpty(lineItems[2]) ? lineItems[2].Trim() : string.Empty,
                                        IsNonReadAction = !string.IsNullOrEmpty(lineItems[3]) ? lineItems[3].Trim() : string.Empty,
                                        IsCrucial = !string.IsNullOrEmpty(lineItems[4]) ? lineItems[4].Trim() : string.Empty
                                    };
                                    testCommands.Add(item);
                                }
                                catch(Exception ex)
                                {
                                    MessageBox.Show("", "File Format not supported!");
                                }
                            }
                        }
                    }

                    lblOpenedTestFileName.Text = fileName;
                    ToggleOpenFileState(true);

                    UpdateDataGrid();
                }
            }
        }

        private void ReadConfigurationFile()
        {
            const string testFileName = "TestFileName";
            const string screenShotSaveFolder = "ScreenShotSaveFolder";
            const string browserType = "BrowserType";
            const string runHeadless = "RunHeadless";
            const string testAllBrowsers = "TestAllBrowsers";
            const string specifyTestFiles = "SpecifyTestFiles";
            const string testFolderName = "TestFolderName";
            const string folderFileFilterType = "FolderFileFilterType";
            const string folderFileFilter = "FolderFileFilter";
            string filter = "Test Configuration Files|*.tconfig|Bak Files|*.bak|All Files|*.*";
            string dialogTitle = "Open Configuration File";
            string fileName = SelectFile(filter, dialogTitle);
            string line;
            string value;

            if (!string.IsNullOrEmpty(fileName))
            {
                txtConfigurationFilePath.Text = fileName.Substring(0, fileName.LastIndexOf("\\"));
                lstTestSettingsFileName.Items.Clear();

                using (StreamReader sr = new StreamReader(fileName))
                {
                    while (!sr.EndOfStream)
                    {
                        line = sr.ReadLine();
                        if (!string.IsNullOrEmpty(line) && !line.StartsWith("//"))
                        {
                            value = line.Substring(line.IndexOf("=") + 1);
                            if (line.StartsWith(testFileName) || line.ToLower().StartsWith(testFileName.ToLower()))
                            {
                                //txtTestFileName.Text = value;
                                lstTestSettingsFileName.Items.Add(value);
                            }
                            else if (line.StartsWith(screenShotSaveFolder) || line.ToLower().StartsWith(screenShotSaveFolder.ToLower()))
                            {
                                txtScreenShotFolder.Text = value;
                            }
                            else if (line.StartsWith(browserType) || line.ToLower().StartsWith(browserType.ToLower()))
                            {
                                cboBrowserType.SelectedIndex = cboBrowserType.FindString(value);
                            }
                            else if (line.StartsWith(runHeadless) || line.ToLower().StartsWith(runHeadless.ToLower()))
                            {
                                cboRunHeadless.SelectedIndex = cboRunHeadless.FindString(value);
                            }
                            else if (line.StartsWith(testAllBrowsers) || line.ToLower().StartsWith(testAllBrowsers.ToLower()))
                            {
                                cboTestAllBrowsers.SelectedIndex = cboTestAllBrowsers.FindString(value);
                            }
                            else if (line.StartsWith(specifyTestFiles) || line.ToLower().StartsWith(specifyTestFiles.ToLower()))
                            {
                                cboSpecifyFilesOrSelectFolder.SelectedIndex = cboSpecifyFilesOrSelectFolder.FindString(value);
                            }
                            else if (line.StartsWith(testFolderName) || line.ToLower().StartsWith(testFolderName.ToLower()))
                            {
                                txtTestFileName.Text = value;
                            }
                            else if (line.StartsWith(folderFileFilterType) || line.ToLower().StartsWith(folderFileFilterType.ToLower()))
                            {
                                cboFileFilterType.SelectedIndex = cboFileFilterType.FindString(value.Replace("_", " "));
                            }
                            else if (line.StartsWith(folderFileFilter) || line.ToLower().StartsWith(folderFileFilter.ToLower()))
                            {
                                txtFolderFilter.Text = value;
                            }
                        }
                    }
                }
            }
        }

        private string SelectFile(string filter, string dialogTitle)
        {
            OpenFileDialog cdFile = new OpenFileDialog();
            cdFile.Filter = filter;
            cdFile.Title = dialogTitle;
            DialogResult rslt = cdFile.ShowDialog();
            if (rslt == DialogResult.OK && !string.IsNullOrEmpty(cdFile.FileName))
            {
                return cdFile.FileName;
            }
            return null;
        }

        private void ToggleOpenFileState(bool isVisibile)
        {
            lblOpenedTestFileName.Visible = isVisibile;
            lblOpenedTestFileNameLabel.Visible = isVisibile;
        }

        private void GetElementXPath(HtmlElementEventArgs e)
        {
            HtmlElement element = this.wbTestPage.Document.GetElementFromPoint(e.ClientMousePosition);

            var savedId = element.Id;
            var uniqueId = Guid.NewGuid().ToString();
            element.Id = uniqueId;

            var doc = new HtmlAgilityPack.HtmlDocument();
            doc.LoadHtml(element.Document.GetElementsByTagName("html")[0].OuterHtml);
            element.Id = savedId;

            var node = doc.GetElementbyId(uniqueId);
            var xpath = node.XPath;

            txtAccessor.Text = xpath;
            txtExpectedValueAction.Text = element.InnerText;
            cboAccessorType.SelectedIndex = cboAccessorType.FindString("xpath");
            cboPerformNonReadAction.SelectedIndex = cboPerformNonReadAction.FindString("false");
            cboCrucialAssertion.SelectedIndex = cboCrucialAssertion.FindString("false");
            grpWebPage.Visible = false;
            elementClicked = true;
        }


        //GAJ working here
        /*
        private void GetXPathForAllElements(string webAddress, string elementType)
        {
            //WebClient webClient = new WebClient();
            //webClient.BaseAddress = webAddress;
            //webClient.OpenRead(webAddress);
            //HtmlElement element = elementType;

            WebRequest webRequest = WebRequest.Create(webAddress);
            var responseFromServer = string.Empty;

            using (WebResponse webResponse = webRequest.GetResponse())
            {
                var responseDataStream = webResponse.GetResponseStream();
                if (responseDataStream != null)
                {
                    var reader = new StreamReader(responseDataStream);

                    responseFromServer = reader.ReadToEnd();

                    reader.Close();
                }
            }


            var doc = new HtmlAgilityPack.HtmlDocument();
            //doc.LoadHtml(element.Document.GetElementsByTagName("html")[0].OuterHtml);
            doc.LoadHtml(responseFromServer);
            foreach (HtmlElement item in doc.)

            

        }
        */

        //private void LoadYesNo(ComboBox cboBx)
        //{
        //    cboBx.Items.Add("Yes");
        //    cboBx.Items.Add("No");
        //}
        #endregion



        private void UpdateDataGrid()
        {
            dgvCommands.DataSource = new List<TestCommand>();
            dgvCommands.Refresh();
            if (testCommands.Count > 0)
            {
                dgvCommands.DataSource = testCommands;
                dgvCommands.Update();
                dgvCommands.AutoResizeColumns();
            }
        }

        #region { Tools Menu -> Add menu items }
        private void mnuToolsAddWaitDelay_Click(object sender, EventArgs e)
        {
            #region {AppConfig Reference}
            /*
             *  <add key="Wait_Accessor" value="n/a"/>
                <add key="Wait_ExpectedValue" value="Wait - 5000"/>
                <add key="Wait_AccessorType" value="n/a"/>
                <add key="Wait_PerformNonReadAction" value="true"/>
                <add key="Wait_Crucial" value="false"/>
             * */
            #endregion
            /*
            txtAccessor.Text = ConfigurationManager.AppSettings["Wait_Accessor"] != null ? ConfigurationManager.AppSettings["Wait_Accessor"] : "n/a";
            txtExpectedValueAction.Text = ConfigurationManager.AppSettings["Wait_ExpectedValue"] != null ? ConfigurationManager.AppSettings["Wait_ExpectedValue"] : "Wait - 5000";
            cboAccessorType.SelectedIndex = cboAccessorType.FindString(ConfigurationManager.AppSettings["Wait_AccessorType"] != null ? ConfigurationManager.AppSettings["Wait_AccessorType"] : "n/a");
            cboPerformNonReadAction.SelectedIndex = cboPerformNonReadAction.FindString(ConfigurationManager.AppSettings["Wait_PerformNonReadAction"] != null ? ConfigurationManager.AppSettings["Wait_PerformNonReadAction"] : "true");
            cboCrucialAssertion.SelectedIndex = cboCrucialAssertion.FindString(ConfigurationManager.AppSettings["Wait_Crucial"] != null ? ConfigurationManager.AppSettings["Wait_Crucial"] : "false");
            */
            string accessor = ConfigurationManager.AppSettings["Wait_Accessor"] != null ? ConfigurationManager.AppSettings["Wait_Accessor"] : "n/a";
            string expectedValueAction = ConfigurationManager.AppSettings["Wait_ExpectedValue"] != null ? ConfigurationManager.AppSettings["Wait_ExpectedValue"] : "Wait - 5000";
            string accessorType = ConfigurationManager.AppSettings["Wait_AccessorType"] != null ? ConfigurationManager.AppSettings["Wait_AccessorType"] : "n/a";
            string performNonReadAction = ConfigurationManager.AppSettings["Wait_PerformNonReadAction"] != null ? ConfigurationManager.AppSettings["Wait_PerformNonReadAction"] : "true";
            string crucialAssertion = ConfigurationManager.AppSettings["Wait_Crucial"] != null ? ConfigurationManager.AppSettings["Wait_Crucial"] : "false";
            SetTestControls(accessor, expectedValueAction, accessorType, performNonReadAction, crucialAssertion);
        }

        private void mnuToolsAddScreenShot_Click(object sender, EventArgs e)
        {
            //n/a ; ScreenShot ; n/a ; true ; false
            SetTestControls("n/a", "ScreenShot", "n/a", "true", "false");
        }

        

        private void mnuToolsAddUrlCheckWithoutNavigation_Click(object sender, EventArgs e)
        {
            //n/a ; URL - https://formy-project.herokuapp.com/thanks ; n/a ; true ; false
            SetTestControls("n/a", "URL - https://YourUrlHere.com/FillItIn", "n/a", "true", "false");
        }

        private void mnuToolsAddNavigationWithCheck_Click(object sender, EventArgs e)
        {
            //https://formy-project.herokuapp.com/form ; Navigate - https://formy-project.herokuapp.com/form ; n/a ; true ; true
            SetTestControls("https://YourUrlHere.com/FillItIn", "Navigate - https://YourUrlHere.com/FillItIn/CouldBeDifferent", "n/a", "true", "true");
        }

        private void mnuToolsAddNavigationWithoutCheck_Click(object sender, EventArgs e)
        {
            //https://formy-project.herokuapp.com/form ; Navigate ; n/a ; true ; true
            SetTestControls("https://YourUrlHere.com/FillItIn", "Navigate", "n/a", "true", "false");
        }

        private void mnuToolsAddNavigationWithCheckIncludingAdditionalWaitTime_Click(object sender, EventArgs e)
        {
            //https://formy-project.herokuapp.com/form ; Navigate - https://formy-project.herokuapp.com/form - 5000; n/a ; true ; true
            SetTestControls("https://YourUrlHere.com/FillItIn", "Navigate - https://YourUrlHere.com/FillItIn/CouldBeDifferent - 5000", "n/a", "true", "true");
        }

        private void mnuToolsAddNavigationWithoutCheckIncludingAdditionalWaitTime_Click(object sender, EventArgs e)
        {
            //https://formy-project.herokuapp.com/form ; Navigate - - 5000; n/a ; true ; true
            SetTestControls("https://YourUrlHere.com/FillItIn", "Navigate - - 5000", "n/a", "true", "false");
        }

        private void mnuToolsAddSendTextToTextInputById_Click(object sender, EventArgs e)
        {
            //first-name; John; ID; true; false
            SetTestControls("first-name", "John", "ID", "true", "false");
        }

        
        private void mnuToolsAddSelectFromDropDownByCssSelector_Click(object sender, EventArgs e)
        {
            //option[value='1'] ; click ; CssSelector ; true ; false
            SetTestControls("option[value='1']", "click", "CssSelector", "true", "false");
        }
        #endregion

        private void SetTestControls(string accessor, string expectedValue, string accessorType, string performAction, string crucialAssertion)
        {
            if (!grpTestCommands.Visible)
            {
                ShowGroupBox(grpTestCommands);
            }
            txtAccessor.Text = accessor;
            txtExpectedValueAction.Text = expectedValue;
            cboAccessorType.SelectedIndex = cboAccessorType.FindString(accessorType);
            cboPerformNonReadAction.SelectedIndex = cboPerformNonReadAction.FindString(performAction);
            cboCrucialAssertion.SelectedIndex = cboCrucialAssertion.FindString(crucialAssertion);
        }

     
    }
}
