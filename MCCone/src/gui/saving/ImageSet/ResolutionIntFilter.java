package gui.saving.ImageSet;

import information.ID;
import information.SharedVariables;

import java.util.logging.Logger;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

import gui.Color_schema;

public class ResolutionIntFilter extends DocumentFilter {
		private ArrowMouseListener arrowMouseListener;
		private int textFieldID=ID.TEXTFIELD_WIDTH;
		private ResolutionIntFilter anotherFilter=null;
		private JTextField anotherField=null;
		private boolean updateAnotherField=true;
		private int maximumImageSize=SharedVariables.IMAGESET_EXPORT_MAX_RESOLUTION;
		private final static Logger LOGGER = Logger.getLogger("MCCLogger");
		
		
	
	public ResolutionIntFilter(ArrowMouseListener aml, int id, JTextField f) {
		this.arrowMouseListener=aml;
		this.textFieldID=id;
		this.anotherField=f;
		
	}
	
	   @Override
	   public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {

	      Document doc = fb.getDocument();
	      StringBuilder sb = new StringBuilder();
	      sb.append(doc.getText(0, doc.getLength()));
	      sb.insert(offset, string);

	      if (test(sb.toString())) {
	    	  if(filterAndUpdateTextFields(sb.toString()))
	         super.insertString(fb, offset, string, attr);
	         
	      } else {
	         // warn the user and don't allow the insert
	      }
	   }

	   private boolean test(String text) {
	      try {
	    	  if(text != null){
	    		  if(text.length() ==0)
	    			  return true; // no text yet
	        int value= Integer.parseInt(text);
	        if(value <= SharedVariables.IMAGESET_EXPORT_MAX_RESOLUTION )
	         return true;
	        else
	        	return false;
	    	  }
	    	  else
	    		  return false;
	      } catch (NumberFormatException e) {
	         return false;
	      }
	   }

	   @Override
	   public void replace(FilterBypass fb, int offset, int length, String text,
	         AttributeSet attrs) throws BadLocationException {

	      Document doc = fb.getDocument();
	      StringBuilder sb = new StringBuilder();
	      sb.append(doc.getText(0, doc.getLength()));
	      sb.replace(offset, offset + length, text);

	      if (test(sb.toString())) {
	    	  if(filterAndUpdateTextFields(sb.toString()))
	         super.replace(fb, offset, length, text, attrs);
	       
	      } else {
	         // warn the user and don't allow the insert
	      }

	   }

	   @Override
	   public void remove(FilterBypass fb, int offset, int length)
	         throws BadLocationException {
	      Document doc = fb.getDocument();
	      StringBuilder sb = new StringBuilder();
	      sb.append(doc.getText(0, doc.getLength()));
	      sb.delete(offset, offset + length);

	      if (test(sb.toString())) {
	    	 if(filterAndUpdateTextFields(sb.toString()))
	         super.remove(fb, offset, length);
	       
	      } else {
	         // warn the user and don't allow the insert
	      }

	   }
	   
	   private boolean filterAndUpdateTextFields(String numberString){
		 boolean ok_resolution=true;
		   // if this filter is allowed to update another field 
		   
		 if(isUpdateAnotherField()){
			 this.anotherFilter.setUpdateAnotherField(false);
			 if(arrowMouseListener != null){
	        	 if(textFieldID==ID.TEXTFIELD_WIDTH){
	        		 System.out.println("ID.textfield.width");
	        		 ok_resolution=updateField(numberString, this.anotherField, false);
	        		 
	        		// arrowMouseListener.updateResolutionFromWidth();     
	        	 }
	        	 else{
	        		ok_resolution= updateField(numberString, this.anotherField, true);
	        		// arrowMouseListener.updateResolutionFromHeight();
	        	 }
	         }
			 this.anotherFilter.setUpdateAnotherField(true);
		   
		 }
		 return ok_resolution;
	   }

	

	public ResolutionIntFilter getAnotherFilter() {
		return anotherFilter;
	}

	public void setAnotherFilter(ResolutionIntFilter anotherFilter) {
		this.anotherFilter = anotherFilter;
	}

	public boolean isUpdateAnotherField() {
		return updateAnotherField;
	}

	public void setUpdateAnotherField(boolean updateAnotherField) {
		this.updateAnotherField = updateAnotherField;
	}
	
	
	private boolean updateField(String field_changed_string, JTextField field_calculated, boolean divide){
		try {
			if(field_changed_string != null && field_changed_string.length()>0){

			
				int presentValue=Integer.parseInt(field_changed_string.trim());
				
				if(presentValue<=maximumImageSize && presentValue > 0){

					if(ArrowMouseListener.scalingFactor !=0){
						int secondValue=0;
						if(divide){
							secondValue= (int)(((double)presentValue)/ArrowMouseListener.scalingFactor);
						}
						else{

							secondValue= (int)(((double)presentValue)*ArrowMouseListener.scalingFactor);
						}
						if(secondValue <=maximumImageSize && secondValue >= 0){
						//	field_changed.setText(""+presentValue);
						//	field_changed.setForeground(Color_schema.white_230);

							field_calculated.setText(""+secondValue);
							if(secondValue>0)
								field_calculated.setForeground(Color_schema.white_230);
							else
								field_calculated.setForeground(Color_schema.orange_dark);
							return true;
						}
						else{
							return false;
						//	field_calculated.setForeground(Color_schema.orange_dark);
							

						}

					}
					else{
						return false;
					//	field_changed.setText(""+presentValue);
					//	field_changed.setForeground(Color_schema.white_230);
					}
					
				}
				else{
					return false;
				}
				
			}
			else{
				field_calculated.setText("0");
				field_calculated.setForeground(Color_schema.orange_dark);
				return true;
			}
			
			} catch (NumberFormatException e) {
				
				LOGGER.warning("Resolution has to be numerical value");
				return false;
			}


		

	}
	
	public void setScalingFactor(double factor){
		ArrowMouseListener.scalingFactor=factor;
	}

	public JTextField getAnotherField() {
		return anotherField;
	}

	public void setAnotherField(JTextField anotherField) {
		this.anotherField = anotherField;
	}
	   
	 
	   
	   
	}
