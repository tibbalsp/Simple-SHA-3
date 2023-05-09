//Authors:   Patrick Tibbals, Iam McLean

public class KMACXOF256 {
    Utils utils = new Utils();

   public String KMACJOB(String K, String X, String S, int L) {
       String newX = utils.bytepad((utils.encode_string(utils.textToHexString(K))), 136) + utils.textToHexString(X) + utils.right_encode(0);
       return new SHAKE256().Sponge(newX,"KMAC",S,L);
   }
}