public class Form {

    public static void main(String[] args) throws Exception {
        Form form = new Form();
        form.RunTestCentral();
    }

    /***************************************************************
     *  Description: Standalone start for the XML configuration
     *              file types. (New)
     * @throws Exception
     ***************************************************************/
    private void RunTestCentral() throws Exception {
        //attempting to add a user interface
        /*JFrame f = new JFrame("A JFrame");
        f.setSize(250, 250);
        f.setLocation(300,200);
        final JTextArea textArea = new JTextArea(10, 40);
        f.getContentPane().add(BorderLayout.CENTER, textArea);
        final JButton button = new JButton("Click Me");
        f.getContentPane().add(BorderLayout.SOUTH, button);
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.append("Button was clicked\n");

            }
        });

        f.setVisible(true);*/
        //end of user interface test

        System.out.println("In RunTestCentral");
        TestCentral testCentral = new TestCentral();
        testCentral.set_executedFromMain(true);
        //testCentral.TestCentralStart();
        testCentral.ConfigurableTestController();

    }
}
