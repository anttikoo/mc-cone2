package gui.file;

import information.ID;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.filechooser.FileFilter;

import operators.XMLreadManager;

public class FileManager {

	public static int checkFile(File file, ArrayList<FileFilter> filters, boolean checkXMLvalidity){
		if(file != null){
			if(file.exists()){
				if(file.isFile()){

					if(filterAccepts(filters, file)){

						if(file.canRead()){
							if(file.length()>0){ // is just created file
								if(file.canWrite()){
									if(checkXMLvalidity){ // if xml file -> check validity
										XMLreadManager rm=new XMLreadManager();
										if(rm.isFileValid(file)){
											return ID.FILE_OK; // can update the file
										}
										else
											return ID.FILE_NOT_VALID;
									}
									else
										return ID.FILE_OK;
								}else // if is not valid file
									return ID.FILE_CANT_WRITE;
							}else
								return ID.FILE_NEW_FILE;
						}else // if can't read
							return ID.FILE_CANT_READ;
					}else // if is not right format
						return ID.FILE_NOT_RIGHT_FORMAT;
				}else //if file is folder
					return ID.FILE_IS_NOT_FILE;
			}else // if file not exist
				return ID.FILE_NEW_FILE;
		}else
			return ID.FILE_CANT_READ;
	}


	private static boolean filterAccepts(ArrayList<FileFilter> filters, File file){
		for (Iterator<FileFilter> iterator = filters.iterator(); iterator.hasNext();) {
			FileFilter fileFilter = (FileFilter) iterator.next();
			if(fileFilter.accept(file))
				return true;
		}
		return false;
	}

	public static boolean accept(String fileName, FileFilter[] filterList){
		if(filterList != null && filterList.length>0)
		for (int i = 0; i < filterList.length; i++) {
			FileFilter filter=filterList[i];
			if(filter.accept(new File(fileName))){
				return true;
			}
		}
		return false;
	}

	public static boolean accept(String fileName, FileFilter filter){
		return accept(fileName, new FileFilter[] {filter});
	}
}
