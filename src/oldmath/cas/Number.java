package oldmath.cas;

class Number extends Calculable{
	double number;
	Number(double num){
		number = num;
	}
	Number(String num){
		number = Double.parseDouble(num);
	}
	Number(Number num){
		number = num.number;
	}
	
	
	static double getNumber(String inp){
		try{
			return Double.parseDouble(inp);
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
	static boolean isPartOfNumber(char c){
		if((c>='0' && c<='9') || c== '.') return true;
		return false;
	}
	
	
	@Override
	public String toString(){
		return number+"";
	}
	
	@Override
	double getValue(){
		return number;
	}
}
