import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



public class RecursiveDescentParser{
    // token is shared between all methods, it tracks which token we are reading
    private static String token;
    // fileTokens has all our tokens, we read from it
    private static BufferedReader fileTokens;
    // tokenIndex tracks the index of tokens, since BufferedReader do not provide such mechanism
    private static int tokenIndex;

    // The method for the non-terminal factorPrime defined in the grammar
    public static Result factorPrime() throws IOException{
        // System.out.println("I am factorPrime");
        if(token.equals("+") || token.equals("-")){
            // System.out.println("factorPrime +");
            token = fileTokens.readLine();
            tokenIndex++;
            return factor();
        }else if(token.equals(";")){ // epsilon case because Follow(factorPrime) = {";"}
            return Result.SUCCESS;
        }
        return Result.ERROR;
    }

  

    // The method for the non-terminal paramsPrime defined in the grammar

    public static Result paramsPrime() throws IOException{
        token = fileTokens.readLine();
        tokenIndex++;
        if (token.equals(",")){
            token = fileTokens.readLine();
            tokenIndex++;
            return params();
        }else if(token.equals(")")){ // epsilon case: Calculating Follow(paramsPrime) = {")"}, if token isnt there then error:
            return Result.SUCCESS;
        }
        return Result.ERROR;
        
    }

    // The method for the non-terminal factor defined in the grammar

    public static Result factor(){
        if (token.equals("id") || token.equals("num")){
            return Result.SUCCESS;
        }
        return Result.ERROR;
    }


    // The method for the non-terminal params defined in the grammar


    public static Result params() throws IOException{
        if(factor().equals(Result.SUCCESS)){
            return paramsPrime();
        }
        return Result.ERROR;
    }

    // The method for the non-terminal procedureCall defined in the grammar


    public static Result procedureCall() throws IOException{
        if (token.equals("id")){
            token = fileTokens.readLine();
            tokenIndex++;
            if (token.equals("(")){
                token = fileTokens.readLine();
                tokenIndex++;
                Result res = params();
                return res;
            }

        }
        return Result.ERROR;
    }
  
    // The method for the non-terminal statementListPrime defined in the grammar


    public static Result statementListPrime(){
        if(statementList().equals(Result.ERROR)){
            if(token.equals("}")){// epsilon case: Follow(statementListPrime) = {"}"}
                return Result.SUCCESS;
            }
        }else{
            return Result.SUCCESS;
        }
        return Result.ERROR;

    }

    // The method for the non-terminal expression defined in the grammar


    public static Result expression() throws IOException{
        token = fileTokens.readLine();
        tokenIndex++;
        if (token.equals("id")){
            token = fileTokens.readLine();
            tokenIndex++;
            if (token.equals("=")){
                token = fileTokens.readLine();
                tokenIndex++;
                Result res = factor();
                if (res.equals(Result.SUCCESS)){
                    token = fileTokens.readLine();
                    tokenIndex++;
                    return factorPrime();
                }
            }
        }
        return Result.ERROR;
    }

    // The method for the non-terminal statement defined in the grammar


    public static Result statement() throws IOException{
        if (token.equals("call")){
            // System.out.println("I am calling" + token);
            token = fileTokens.readLine();
            tokenIndex++;
            if (token.equals(":")){
                token = fileTokens.readLine();
                tokenIndex++;
                return procedureCall();               
            }      
        } else if (token.equals("compute")){
            token = fileTokens.readLine();
            tokenIndex++;
            if (token.equals(":")){
                return expression();
            }
        }  
        return Result.ERROR;
    }

    // The method for the non-terminal "statement;", here refered to as statementSC()


    public static Result statementSC() throws IOException{
        if(statement().equals(Result.ERROR)){
            // System.out.println("the token here  "+token);
            return Result.ERROR;
        }else{
            if (token.equals(";")){
                // System.out.println("epsilon case");
                return Result.SUCCESS;//epsilon case detected by factorPrime
            }else if(token.equals(";") == false){// factor prime existed, so ; wasnt verified by factorPrime
                token = fileTokens.readLine();
                tokenIndex++;
                if(token.equals(";")){
                    // System.out.println("No epsilon detected");
                    return Result.SUCCESS;
                }else{
                    return Result.ERROR;
                }
            }
            return Result.ERROR;
        }
    }

    // The method for the non-terminal statementList defined in the grammar

    public static Result statementList(){
        try{
            if(statementSC().equals(Result.ERROR)){
                return Result.ERROR;
            }else{
                token = fileTokens.readLine();
                tokenIndex++;
                if (statementListPrime().equals(Result.ERROR)){
                    return Result.ERROR;
                }else{
                    return Result.SUCCESS;
                }

            }
        }catch(IOException e){
            System.out.println("errors of reading files");
        }
        return Result.ERROR;
    }

    
    // The method for the non-terminal program defined in the grammar

    public static Result program(){
        try{
            if (token.equals("{")){
                token = fileTokens.readLine();
                tokenIndex++;
                Result res = statementList();
                return res;
            }
            else {
                return Result.ERROR;
            }
        }catch(IOException e){
            System.out.println("errors of reading files");
        }
        return Result.ERROR;
    }



    // start the program


    public static Result start(){
        try{
            token = fileTokens.readLine();
            tokenIndex++;
            Result res = program();


            if (res.equals(Result.ERROR)){
                return res;
            } 
            token = fileTokens.readLine();
            tokenIndex++;
            if(token.equals("$") == false){// ensure no additional characters are present after end of file
                //result coming from all the processing
                // System.out.println("res: "+res);
                return Result.ERROR;
            } else {
                return Result.SUCCESS;
            }
        }catch(IOException e){
            System.out.println("errors of reading file");
        }
        return Result.ERROR;
    }



    // The main method that read the test files line by line
    public static void main(String[] args){
        if (args.length == 0){
            System.out.println("please enter the name of the file that contains tokens (input1.txt,input2.txt or input3.txt)");
        }
        else{
            System.out.println("The program is starting to parse the file: "+args[0]);
            String fileName = args[0];
            try {
                fileTokens = new BufferedReader(new FileReader(fileName));
                tokenIndex = -1;

                Result result = start();

                // System.out.println("file tokens content");
                // System.out.println(token);

                // System.out.println("the result is "+ result);
                if(result.equals(Result.ERROR)){
                    System.out.println("ERROR: the code contains a syntax mistake");

                    System.out.println("There was a problem with token: "+token+" at position: "+ tokenIndex);
                }else{
                    System.out.println("SUCCESS: the code has been successfully parsed");

                    System.out.println("The last token to be read was: "+token);
                }
                fileTokens.close();
            } catch(IOException e){
                System.out.println("An IO Exception occured");
            }

        }
    }
}

enum Result {
    SUCCESS,
    ERROR
}