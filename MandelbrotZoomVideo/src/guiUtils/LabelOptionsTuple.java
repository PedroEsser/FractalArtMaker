package guiUtils;

import javax.swing.JComboBox;
import javax.swing.JLabel;

public class LabelOptionsTuple extends LabelTuple<JComboBox<String>>{
	
	public LabelOptionsTuple(String label, String... options) {
		super(label, new JComboBox<String>(options));
	}
	
	public void setOption(String option) {
		getRight().setSelectedItem(option);
	}
	
	public String getSelectedOption() {
		return getRight().getSelectedItem().toString();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		getRight().setEditable(false);
	}

}
