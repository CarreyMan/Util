package util;

public enum IntTypeM {
    _360("0","360"),
    _365("1","365");


    private final String key;
    private final String value;

    public String getKey(){
        return key;
    }

    public String getValue()
    {
        return value;
    }

    IntTypeM(String key, String value){
        this.key = key;
        this.value = value;
    }

    public static String getValue(String key){
        for(IntTypeM atte : IntTypeM.values()){
            if(atte.getKey().equals(key)){
                return atte.getValue();
            }
        }
        return null;
    }
}