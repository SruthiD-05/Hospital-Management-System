import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class StudentMarksManager extends JFrame {
    private final List<Student> students = new ArrayList<>();
    private final List<Subject> subjects = new ArrayList<>();
    private final List<Exam> exams = new ArrayList<>();
    private final Map<String, Double> marks = new HashMap<>();
    private CardLayout cardLayout;
    private JPanel cards;
    private DefaultTableModel studentTableModel, subjectTableModel, examTableModel, marksTableModel, reportTableModel;
    private JTable studentTable, subjectTable, examTable, marksTable, reportTable;
    private JTextField txtStudentRoll, txtStudentName, txtStudentEmail, txtParentEmail;
    private JTextField txtSubjectCode, txtSubjectName;
    private JTextField txtExamName, txtTotalMarks;
    private JComboBox<String> cmbStudentsForMarks, cmbSubjectsForMarks, cmbExamsForMarks;
    private JTextField txtMarkValue;
    private JLabel lblAverage;
    private final String TEACHER_USER = "teacher";
    private final String TEACHER_PASS = "teach123";

    public StudentMarksManager() {
        setTitle("🎓 Student Marks Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setResizable(false);
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.add(createLoginPanel(), "login");
        cards.add(createDashboardPanel(), "dashboard");
        add(cards);
        cardLayout.show(cards, "login");
        setVisible(true);
    }

    private JPanel createLoginPanel() {
        GradientPanel p = new GradientPanel(new Color(255, 170, 200), new Color(255, 220, 240));
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        JLabel title = new JLabel("🎓 Teacher Login Portal");
        title.setFont(new Font("Serif", Font.BOLD, 36));
        title.setForeground(new Color(70, 0, 70));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        p.add(title, gbc);

        gbc.gridwidth = 1; gbc.gridy++;
        p.add(new JLabel("👤 Username:"), gbc);
        gbc.gridx = 1;
        JTextField user = new JTextField(16);
        p.add(user, gbc);

        gbc.gridx = 0; gbc.gridy++;
        p.add(new JLabel("🔒 Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField pass = new JPasswordField(16);
        p.add(pass, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        JButton btnLogin = createPrimaryButton("Login", new Color(180, 30, 120));
        btnLogin.addActionListener(e -> {
            if (user.getText().equals(TEACHER_USER) && new String(pass.getPassword()).equals(TEACHER_PASS)) {
                cardLayout.show(cards, "dashboard");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Invalid credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        p.add(btnLogin, gbc);
        return p;
    }

    private JPanel createDashboardPanel() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabs.addTab("👩‍🎓 Students", createStudentsPanel());
        tabs.addTab("📘 Subjects", createSubjectsPanel());
        tabs.addTab("🧮 Exams", createExamsPanel());
        tabs.addTab("✏️ Marks Entry", createMarksEntryPanel());
        tabs.addTab("📊 Reports", createReportsPanel());
        JPanel outer = new JPanel(new BorderLayout());
        JLabel header = new JLabel("🏫 Student Marks Management Dashboard", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(40, 0, 80));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Serif", Font.BOLD, 26));
        header.setBorder(new EmptyBorder(12, 12, 12, 12));
        outer.add(header, BorderLayout.NORTH);
        outer.add(tabs, BorderLayout.CENTER);
        return outer;
    }

    private JPanel createStudentsPanel() {
        GradientPanel p = new GradientPanel(new Color(200, 240, 255), new Color(240, 255, 255));
        p.setLayout(new BorderLayout(12, 12));
        studentTableModel = new DefaultTableModel(new Object[]{"Roll No", "Student Name", "Email", "Parent Email"}, 0);
        studentTable = new JTable(studentTableModel);
        styleTable(studentTable);

        txtStudentRoll = new JTextField();
        txtStudentName = new JTextField();
        txtStudentEmail = new JTextField();
        txtParentEmail = new JTextField();

        JButton btnAdd = createPrimaryButton("➕ Add Student", new Color(0,140,180));
        btnAdd.addActionListener(e -> {
            String roll = txtStudentRoll.getText().trim();
            String name = txtStudentName.getText().trim();
            String email = txtStudentEmail.getText().trim();
            String parent = txtParentEmail.getText().trim();
            if (roll.isEmpty() || name.isEmpty() || email.isEmpty() || parent.isEmpty()) {
                showErr("Enter all details!");
                return;
            }
            students.add(new Student(roll, name, email, parent));
            studentTableModel.addRow(new Object[]{roll, name, email, parent});
            txtStudentRoll.setText(""); txtStudentName.setText("");
            txtStudentEmail.setText(""); txtParentEmail.setText("");
            refreshCombos();
        });
        JPanel form = createFormPanel(
                new String[]{"Roll No:", "Name:", "Email:", "Parent Email:"},
                new JTextField[]{txtStudentRoll, txtStudentName, txtStudentEmail, txtParentEmail}, btnAdd);
        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel createSubjectsPanel() {
        GradientPanel p = new GradientPanel(new Color(255, 230, 250), new Color(255, 240, 250));
        p.setLayout(new BorderLayout(12,12));
        subjectTableModel = new DefaultTableModel(new Object[]{"Code", "Subject Name"}, 0);
        subjectTable = new JTable(subjectTableModel);
        styleTable(subjectTable);
        txtSubjectCode = new JTextField();
        txtSubjectName = new JTextField();
        JButton btnAdd = createPrimaryButton("📘 Add Subject", new Color(200,60,140));
        btnAdd.addActionListener(e -> {
            String code = txtSubjectCode.getText().trim();
            String name = txtSubjectName.getText().trim();
            if (code.isEmpty() || name.isEmpty()) { showErr("Enter both Code and Name."); return; }
            subjects.add(new Subject(code, name));
            subjectTableModel.addRow(new Object[]{code, name});
            txtSubjectCode.setText(""); txtSubjectName.setText("");
            refreshCombos();
        });
        JPanel form = createFormPanel(new String[]{"Code:", "Name:"}, new JTextField[]{txtSubjectCode, txtSubjectName}, btnAdd);
        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(subjectTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel createExamsPanel() {
        GradientPanel p = new GradientPanel(new Color(255,255,210), new Color(255,240,180));
        p.setLayout(new BorderLayout(12,12));
        examTableModel = new DefaultTableModel(new Object[]{"Exam Name", "Total Marks"}, 0);
        examTable = new JTable(examTableModel);
        styleTable(examTable);
        txtExamName = new JTextField();
        txtTotalMarks = new JTextField();
        JButton btnAdd = createPrimaryButton("🧮 Add Exam", new Color(255,140,0));
        btnAdd.addActionListener(e -> {
            String name = txtExamName.getText().trim();
            String tot = txtTotalMarks.getText().trim();
            if (name.isEmpty() || tot.isEmpty()) { showErr("Enter exam name and total marks."); return; }
            try {
                double total = Double.parseDouble(tot);
                exams.add(new Exam(name, total));
                examTableModel.addRow(new Object[]{name, total});
                txtExamName.setText(""); txtTotalMarks.setText("");
                refreshCombos();
            } catch (NumberFormatException ex) {
                showErr("Total marks must be a number.");
            }
        });
        JPanel form = createFormPanel(new String[]{"Exam Name:", "Total Marks:"}, new JTextField[]{txtExamName, txtTotalMarks}, btnAdd);
        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(examTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel createMarksEntryPanel() {
        GradientPanel p = new GradientPanel(new Color(230,255,230), new Color(200,240,210));
        p.setLayout(new BorderLayout(12,12));
        marksTableModel = new DefaultTableModel(new Object[]{"Student", "Subject", "Exam", "Marks"}, 0);
        marksTable = new JTable(marksTableModel);
        styleTable(marksTable);
        cmbStudentsForMarks = new JComboBox<>();
        cmbSubjectsForMarks = new JComboBox<>();
        cmbExamsForMarks = new JComboBox<>();
        txtMarkValue = new JTextField();
        JButton btnAdd = createPrimaryButton("💾 Save Marks", new Color(0,160,90));
        btnAdd.addActionListener(e -> {
            if (cmbStudentsForMarks.getItemCount() == 0) { showErr("Add students first."); return; }
            String studentName = (String) cmbStudentsForMarks.getSelectedItem();
            String subjectName = (String) cmbSubjectsForMarks.getSelectedItem();
            String examName = (String) cmbExamsForMarks.getSelectedItem();
            double mark;
            try { mark = Double.parseDouble(txtMarkValue.getText().trim()); }
            catch (Exception ex) { showErr("Enter numeric marks."); return; }
            String key = studentName + "|" + subjectName + "|" + examName;
            marks.put(key, mark);
            marksTableModel.addRow(new Object[]{studentName, subjectName, examName, mark});
            txtMarkValue.setText("");
            JOptionPane.showMessageDialog(this, "✅ Mark saved.");
        });
        JPanel form = new JPanel(new GridLayout(5,2,10,10));
        form.add(new JLabel("Student:")); form.add(cmbStudentsForMarks);
        form.add(new JLabel("Subject:")); form.add(cmbSubjectsForMarks);
        form.add(new JLabel("Exam:")); form.add(cmbExamsForMarks);
        form.add(new JLabel("Marks:")); form.add(txtMarkValue);
        form.add(new JLabel()); form.add(btnAdd);
        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(marksTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel createReportsPanel() {
        GradientPanel p = new GradientPanel(new Color(240,250,255), new Color(220,235,255));
        p.setLayout(new BorderLayout(12,12));
        lblAverage = new JLabel("Average: N/A", SwingConstants.CENTER);
        lblAverage.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblAverage.setForeground(new Color(0, 102, 0));
        JButton btnGenerate = createPrimaryButton("📊 Generate Report", new Color(0,102,204));
        JButton btnSave = createPrimaryButton("💾 Save Report", new Color(140,50,200));
        btnGenerate.addActionListener(e -> generateReport());
        btnSave.addActionListener(e -> saveReportToFile());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.add(btnGenerate); top.add(btnSave);
        reportTableModel = new DefaultTableModel();
        reportTable = new JTable(reportTableModel);
        styleTable(reportTable);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(reportTable), BorderLayout.CENTER);
        p.add(lblAverage, BorderLayout.SOUTH);
        return p;
    }

    private void generateReport() {
        if (students.isEmpty() || subjects.isEmpty() || exams.isEmpty()) {
            showErr("Add all details before generating report."); return;
        }
        Vector<String> cols = new Vector<>();
        cols.add("Student Name");
        for (Subject s : subjects) cols.add(s.name);
        cols.add("Average");
        cols.add("Overall %");
        reportTableModel.setColumnIdentifiers(cols);
        reportTableModel.setRowCount(0);
        double best = -1; String topper = "N/A";
        for (Student st : students) {
            Vector<Object> row = new Vector<>();
            row.add(st.name);
            double totalObt = 0, totalFull = 0;
            int subjCount = 0;
            for (Subject sub : subjects) {
                double sum=0, full=0;
                for (Exam ex : exams) {
                    Double m = marks.get(st.name+"|"+sub.name+"|"+ex.name);
                    if (m!=null) { sum+=m; full+=ex.totalMarks; }
                }
                if (full>0){ row.add(String.format("%.2f/%.0f",sum,full));
                    totalObt+=sum; totalFull+=full; subjCount++;
                } else row.add("-");
            }
            double avg=(subjCount>0)?totalObt/subjCount:0;
            double pct=(totalFull>0)?(totalObt/totalFull)*100:0;
            row.add(String.format("%.2f",avg));
            row.add(String.format("%.2f",pct));
            reportTableModel.addRow(row);
            if(pct>best){best=pct;topper=st.name;}
        }
        lblAverage.setText("🏆 Topper: "+topper+"  |  Highest %: "+String.format("%.2f",best));
    }

    private void saveReportToFile() {
        if (reportTableModel.getRowCount()==0){showErr("Generate report first.");return;}
        JFileChooser fc=new JFileChooser();
        fc.setDialogTitle("Save report as CSV");
        if(fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
            File f=fc.getSelectedFile();
            if(!f.getName().endsWith(".csv"))f=new File(f+".csv");
            try(FileWriter fw=new FileWriter(f)){
                for(int c=0;c<reportTableModel.getColumnCount();c++){
                    fw.write(reportTableModel.getColumnName(c)+(c<reportTableModel.getColumnCount()-1?",":""));
                }
                fw.write("\n");
                for(int r=0;r<reportTableModel.getRowCount();r++){
                    for(int c=0;c<reportTableModel.getColumnCount();c++){
                        fw.write(String.valueOf(reportTableModel.getValueAt(r,c)));
                        if(c<reportTableModel.getColumnCount()-1)fw.write(",");
                    }
                    fw.write("\n");
                }
                JOptionPane.showMessageDialog(this,"✅ Saved to: "+f.getAbsolutePath());
            }catch(Exception ex){showErr(ex.getMessage());}
        }
    }

    private JPanel createFormPanel(String[] labels, JTextField[] fields, JButton button){
        JPanel form=new JPanel(new GridLayout(labels.length+1,2,8,8));
        for(int i=0;i<labels.length;i++){
            form.add(new JLabel(labels[i]));
            form.add(fields[i]);
        }
        form.add(new JLabel());form.add(button);
        return form;
    }

    private JButton createPrimaryButton(String text, Color bg){
        JButton b=new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorder(new RoundBorder(bg.darker()));
        return b;
    }

    private void styleTable(JTable t){
        t.setRowHeight(25);
        JTableHeader h=t.getTableHeader();
        h.setBackground(new Color(70,70,160));h.setForeground(Color.WHITE);
        h.setFont(new Font("SansSerif",Font.BOLD,13));
        t.setFont(new Font("SansSerif",Font.PLAIN,13));
    }

    private void refreshCombos(){
        if(cmbStudentsForMarks==null)return;
        cmbStudentsForMarks.removeAllItems();
        for(Student s:students)cmbStudentsForMarks.addItem(s.name);
        cmbSubjectsForMarks.removeAllItems();
        for(Subject s:subjects)cmbSubjectsForMarks.addItem(s.name);
        cmbExamsForMarks.removeAllItems();
        for(Exam e:exams)cmbExamsForMarks.addItem(e.name);
    }

    private void showErr(String msg){JOptionPane.showMessageDialog(this,msg,"Error",JOptionPane.ERROR_MESSAGE);}

    public static void main(String[] args){SwingUtilities.invokeLater(StudentMarksManager::new);}

    static class Student implements Serializable{
        String roll,name,email,parentEmail;
        Student(String r,String n,String e,String p){roll=r;name=n;email=e;parentEmail=p;}
    }
    static class Subject implements Serializable{String code,name;Subject(String c,String n){code=c;name=n;}}
    static class Exam implements Serializable{String name;double totalMarks;Exam(String n,double t){name=n;totalMarks=t;}}
    static class GradientPanel extends JPanel{
        private final Color c1,c2;
        GradientPanel(Color c1,Color c2){this.c1=c1;this.c2=c2;}
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g.create();
            GradientPaint gp=new GradientPaint(0,0,c1,getWidth(),getHeight(),c2);
            g2.setPaint(gp);g2.fillRect(0,0,getWidth(),getHeight());g2.dispose();
        }
    }
    static class RoundBorder extends LineBorder{
        RoundBorder(Color c){super(c,2,true);}
    }
}
