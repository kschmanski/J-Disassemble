/**
 * Class used to represent a Disassembled element.
 * Contains a String that is the value to print (a concatenation of the memory address, the opcode mnemonic and the arguments)
 * and the number of bytes that the element takes up.
 */
class DisassembledElement {
    public String valueToPrint;
    public int numberOfBytes;

    public DisassembledElement() {
    }

    public void setValueToPrint(String valueToPrint) {
        this.valueToPrint = valueToPrint;
    }

    public void setNumberOfBytes(int numberOfBytes) {
        this.numberOfBytes = numberOfBytes;
    }

}