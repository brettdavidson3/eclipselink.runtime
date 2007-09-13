package defaultPackage;

import org.eclipse.persistence.sdo.SDODataObject;

public class UsAddressTypeImpl extends defaultPackage.AddressTypeImpl implements UsAddressType {

   public static final int START_PROPERTY_INDEX = defaultPackage.AddressTypeImpl.END_PROPERTY_INDEX + 1;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 0;

   public UsAddressTypeImpl() {}

   public int getZip() {
      return getInt(START_PROPERTY_INDEX + 0);
   }

   public void setZip(int value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }


}

