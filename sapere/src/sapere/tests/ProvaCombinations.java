package sapere.tests;

public class ProvaCombinations {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//TEST DATA
		String[] a = new String[]{"1","2","3"};
		String[] b = new String[]{"a","b","c"};
		String[] c = new String[]{"X","Y","Z","Q"};
		String[][] ll = new String[][]{a,b,c};
		
		//METHOD
		int[] indexes = new int[ll.length];
		int combs = 1;
		for(int i=0;i<ll.length;i++)
			combs*=ll[i].length;

		String[][] oo = new String[combs][];
		
		for(int i=0;i<oo.length;i++){
			String[] tmp = new String[ll.length];
			for(int j=0;j<ll.length;j++)
				tmp[j]=ll[j][indexes[j]];
			oo[i]=tmp;
			boolean carry = true;
			for(int j=ll.length-1;j>=0;j--){
				if(carry){
					indexes[j]++;
					carry=false;
					if(indexes[j]==ll[j].length){
						indexes[j]=0;
						carry = true;
					}
				}		
			}
		}
		
		for(String[] arr:oo){
			for(String s:arr)
				System.out.print(s+",");
			System.out.println();
		}
		
		System.out.println(oo.length);
	}
}
