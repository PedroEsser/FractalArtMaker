package guiUtils;

import javax.swing.JComboBox;
import javax.swing.JLabel;

public class LabelOptionsTuple extends LabelTuple<JComboBox<String>>{
	
	public LabelOptionsTuple(String label, String... options) {
		super(label, new JComboBox<String>(options));
	}

}
