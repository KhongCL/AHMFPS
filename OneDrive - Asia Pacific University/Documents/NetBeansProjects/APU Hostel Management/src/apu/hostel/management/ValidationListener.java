package apu.hostel.management;

public class ValidationListener implements javax.swing.event.DocumentListener {
    private Runnable validationFunction;

    public ValidationListener(Runnable validationFunction) {
        this.validationFunction = validationFunction;
    }

    @Override
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        validationFunction.run(); 
    }

    @Override
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        validationFunction.run();
    }

    @Override
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        validationFunction.run();
    }
}