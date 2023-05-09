//Authors:   Patrick Tibbals, Iam McLean


class SHAKE256 {
    Utils utils = new Utils();
    Keccak keccak = new Keccak();
    public String Sponge(String X, String N, String S , int L){
        String input = "";
        String temp = "";
        String bytePad= "";
        String output = "";

        int finalSize = L;
        
        bytePad = utils.keccakInput(N, S, 136);      
        long[] state = utils.hexToLong(bytePad);
        
        state = keccak.sha3_keccakf(state);

        while(X.length()-272 > 0){
            input = X.substring(0,272);
            X = X.substring(272);
            temp = utils.XORhex(input,utils.longToHex(state));
            state = keccak.sha3_keccakf(utils.hexToLong(temp));
        }

        input = X;
        temp = utils.XORhex(utils.padInput(input),utils.longToHex(state));
        state = keccak.sha3_keccakf(utils.hexToLong(temp));

        while(finalSize-272 > 0){
            finalSize-=272;
            output += utils.longToHex(state).substring(0,272);
            state = keccak.sha3_keccakf(state);
        }

        output += utils.longToHex(state).substring(0,L/2);

        return output;

    }
   
}