package guiUtils;

import java.util.function.Consumer;

import javax.swing.JComboBox;
import javax.swing.JLabel;

public class LabelOptionsTuple extends LabelTuple<JComboBox<String>>{
	
	public LabelOptionsTuple(String label, String... options) {
		super(label, new JComboBox<String>(options));
		
	}
	
	public LabelOptionsTuple(String label, Object... options) {
		super(label, new JComboBox<String>(toString(options)));
	}
	
	public void setOption(String option) {
		getRight().setSelectedItem(option);
	}
	
	public String getSelectedOption() {
		return getRight().getSelectedItem().toString();
	}
	
	public void setSelectCallback(Consumer<String> selectCallback) {
		this.getRight().addActionListener(a -> {
			selectCallback.accept(getSelectedOption());
		});
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		getRight().setEditable(false);
	}
	
	private static String[] toString(Object[] obj) {
		String[] strings = new String[obj.length];
		for(int i = 0 ; i < obj.length ; i++)
			strings[i] = obj[i].toString();
		return strings;
	}

}
