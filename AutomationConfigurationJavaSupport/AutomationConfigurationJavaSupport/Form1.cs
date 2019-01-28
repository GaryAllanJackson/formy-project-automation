using AutomationConfigurationJavaSupport.Entities;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace AutomationConfigurationJavaSupport
{
    public partial class frmAutomationConfigurationJavaSupport : Form
    {
        public static string ConfigurationFileName = "testSetup.config";
        //List<string> fileComments = new List<string>();
        string[] comments;
        //make this a binding list so that the clicking the grid doesn't throw IndexOutOfRangeException
        BindingList<TestCommand> testCommands;
        string[] testCommandComments;
        bool elementClicked = false;

        public frmAutomationConfigurationJavaSupport()
        {
            InitializeComponent();
        }

        private void frmAutomationConfigurationJavaSupport_Load(object sender, EventArgs e)
        {
            //configuration specific items
            LoadTrueFalse(cboRunHeadless);
            LoadTrueFalse(cboTestAllBrowsers);
            LoadBrowsers(cboBrowserType);
            PopulateFileComments();
            PopulateTestCommandComments();

            ConfigureGroupBoxes();

            //Test Command specific items
            InitializeGrid();
            LoadTrueFalse(cboPerformNonReadAction);
            LoadAccessorTypes(cboAccessorType);
            LoadTrueFalse(cboCrucialAssertion);
        }




        private void dgvCommands_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            //if (e.RowIndex > 0)
            //{
            //    DataGridViewCell cell = dgvCommands[e.ColumnIndex, e.RowIndex];
            //}
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
            SelectFile(txtTestFileName);
        }

        private void btnSaveConfigurationSettings_Click(object sender, EventArgs e)
        {
            SaveConfigurationFile();
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
        }

        private void mnuViewFormTestSettingCommands_Click(object sender, EventArgs e)
        {
            grpTestCommands.Visible = true;
            grpTestCommands.BringToFront();
            grpConfiguration.Visible = false;
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


                dgvCommands.DataSource = new List<TestCommand>();
                dgvCommands.Refresh();
                if (testCommands.Count > 0)
                {
                    dgvCommands.DataSource = testCommands;
                    dgvCommands.Update();
                    dgvCommands.AutoResizeColumns();
                }
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

                TestCommand item = (from t in testCommands
                                    where t.Accessor.Equals(accessor) &&
                                         t.AccessorType.Equals(accessorType) &&
                                         t.ExpectedValueAction.Equals(expected) &&
                                         t.IsNonReadAction.Equals(isAction) &&
                                         t.IsCrucial.Equals(isCrucial)
                                    select t).FirstOrDefault();

                testCommands.Remove(item);
                dgvCommands.DataSource = null;
                dgvCommands.DataSource = testCommands;
                //dgvCommands.Refresh();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error:" + ex.Message, "Unusual Error with Grid");
            }
        }
        #endregion


        #region { Helper Methods }
        //private void LoadYesNo(ComboBox cboBx)
        //{
        //    cboBx.Items.Add("Yes");
        //    cboBx.Items.Add("No");
        //}

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
            cboBx.Items.Add("n/a");
        }


        private void SelectFolder(TextBox txtBx)
        {
            FolderBrowserDialog fld = new FolderBrowserDialog();
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
            if (!string.IsNullOrEmpty(txtConfigurationFilePath.Text) && !string.IsNullOrEmpty(txtScreenShotFolder.Text) && !string.IsNullOrEmpty(txtTestFileName.Text) &&
                (cboBrowserType.SelectedIndex > -1 || cboBrowserType.Enabled == false) && (cboRunHeadless.SelectedIndex > -1 || cboRunHeadless.Enabled == false)
                && cboTestAllBrowsers.SelectedIndex > -1)
            {
                for (int x = 0; x < comments.GetUpperBound(0); x++)
                {
                    sb.AppendLine(comments[x]);
                }

                sb.AppendLine(string.Format("TestFileName={0}", txtTestFileName.Text));
                sb.AppendLine(string.Format("ScreenShotSaveFolder={0}", txtScreenShotFolder.Text));
                sb.AppendLine(string.Format("BrowserType={0}", cboBrowserType.SelectedItem.ToString()));
                sb.AppendLine(string.Format("RunHeadless={0}", cboRunHeadless.SelectedItem.ToString()));
                sb.AppendLine(string.Format("testAllBrowsers={0}", cboTestAllBrowsers.SelectedItem.ToString()));

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
            comments = new String[]{ "//NOTES: all lines beginning with double slashes will be ignored by the configuration reader",
                                  "//BrowserType values: Firefox, Chrome, PhantomJS",
                                    "//All, will cycle through all browsers",
                                "//TestPageRoot - site page to be tested",
                                "//RunHeadless can be true to run headless or false to show the browser, but PhantomJs is always headless",
                                "//ScreenShotSaveFolder - folder where screenshots should be saved - Must already exist",
                                "//testAllBrowsers - can be true or false.  If false, BrowserType must be set.  If true, BrowserType is ignored.",
                                "//testFileName - name of the file containing the test settings file which is a colon separated file with the following structure:",
                                "// [xPath] : [Expected Value]" };
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
            dgvCommands.DataSource = null;

            dgvCommands.DataSource = testCommands;
            dgvCommands.Update();
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
            cdFile.Filter = "Text Files|*.txt | All Files|*.*";
            cdFile.Title = "Save Test Commands File";

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
                        }
                    }

                    lblOpenedTestFileName.Text = fileName;
                    ToggleOpenFileState(true);

                    dgvCommands.DataSource = new List<TestCommand>();
                    dgvCommands.Refresh();
                    if (testCommands.Count > 0)
                    {
                        dgvCommands.DataSource = testCommands;
                        dgvCommands.Update();
                        //dgvCommands.AutoResizeColumns();
                    }
                }
            }
        }

        private void ReadConfigurationFile()
        {
            const string testFileName = "TestFileName";
            const string screenShotSaveFolder = "ScreenShotSaveFolder";
            const string browserType = "BrowserType";
            const string runHeadless = "RunHeadless";
            const string testAllBrowsers = "testAllBrowsers";
            string filter = "Configuration Files|*.config|Bak Files|*.bak|All Files|*.*";
            string dialogTitle = "Open Configuration File";
            string fileName = SelectFile(filter, dialogTitle);
            string line;
            string value;
            if (!string.IsNullOrEmpty(fileName))
            {
                txtConfigurationFilePath.Text = fileName.Substring(0, fileName.LastIndexOf("\\"));

                using (StreamReader sr = new StreamReader(fileName))
                {
                    while (!sr.EndOfStream)
                    {
                        line = sr.ReadLine();
                        if (!string.IsNullOrEmpty(line) && !line.StartsWith("//"))
                        {
                            value = line.Substring(line.IndexOf("=") + 1);
                            if (line.StartsWith(testFileName))
                            {
                                txtTestFileName.Text = value;
                            }
                            else if (line.StartsWith(screenShotSaveFolder))
                            {
                                txtScreenShotFolder.Text = value;
                            }
                            else if (line.StartsWith(browserType))
                            {
                                cboBrowserType.SelectedIndex = cboBrowserType.FindString(value);
                            }
                            else if (line.StartsWith(runHeadless))
                            {
                                cboRunHeadless.SelectedIndex = cboRunHeadless.FindString(value);
                            }
                            else if (line.StartsWith(testAllBrowsers))
                            {
                                cboTestAllBrowsers.SelectedIndex = cboTestAllBrowsers.FindString(value);
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



        #endregion

        private void txtURL_KeyPress(object sender, KeyPressEventArgs e)
        {

            if (e.KeyChar == (char)13)
            {
                wbTestPage.Navigate(txtURL.Text);
                
            }
        }

        private void wbTestPage_DocumentCompleted(object sender, WebBrowserDocumentCompletedEventArgs e)
        {
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

        private void btnGetXPath_Click(object sender, EventArgs e)
        {
            grpWebPage.Visible = true;
            grpWebPage.BringToFront();
            elementClicked = false;

        }
    }
}
