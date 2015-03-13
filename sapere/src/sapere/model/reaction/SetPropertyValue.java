package sapere.model.reaction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SetPropertyValue extends PropertyValue {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2454059366830058413L;
	private List<PropertyValue> valueSet;
	
	public SetPropertyValue(){
		valueSet = new LinkedList<PropertyValue>();
	}
	
	public SetPropertyValue(List<PropertyValue> valueSet) {
		this.valueSet = valueSet;
	}

	public PropertyValue[] getValues() {
		return valueSet.toArray(new PropertyValue[valueSet.size()]);
	}

	public void setValues(List<PropertyValue> valueSet) {
		this.valueSet = valueSet;
	}
	
	public boolean containsValue(PropertyValue pv) {
		for(PropertyValue lpv:valueSet)
			if(lpv.equals(pv))
				return true;
		return false;
	}

	public void addValue(String v){
		valueSet.add(new SinglePropertyValue(v));
	}
	
	public void addValue(PropertyValue v){
		valueSet.add(v);
	}
	
	public void removeValue(PropertyValue v){
		ArrayList<PropertyValue> l = new ArrayList<PropertyValue>();
		for(PropertyValue s:valueSet){
			if(s.equals(v))
				l.add(s);
		}
		valueSet.removeAll(l);
	}
	
	public void removeValue(String v){
		removeValue(new SinglePropertyValue(v));
	}
	
	public void addValues(PropertyValue[] values){		
		for(PropertyValue value:values){
			addValue(value);
		}
	}
	
	public void removeValues(PropertyValue[] values){
		for(PropertyValue value:values){
			removeValue(value);
		}
	}
	
	public String toString(){
		String tmp = "[";
		int i=0;
		for(;i<valueSet.size()-1;i++)
			tmp+=valueSet.get(i)+",";
		tmp+=valueSet.get(i)+"]";
		return tmp;
	}

	@Override
	public PropertyValue getCopy() {
		SetPropertyValue spv = new SetPropertyValue();
		for(PropertyValue v:this.getValues())
			spv.addValue(v.getCopy());
		return spv;
	}

	@Override
	public boolean equals(Object p) {
		boolean equals = true;
		equals &= p instanceof SetPropertyValue;
		if(equals){
			SetPropertyValue p1 = (SetPropertyValue)p;
			PropertyValue[] p1vs = p1.getValues();
			PropertyValue[] pvs = this.getValues();
			if(p1vs.length==pvs.length){
				for(int i=0;i<p1vs.length;i++){
					equals&=p1vs[i].equals(pvs[i]);
				}
				return equals;
			}
		}
			
		return false;
	}
}
