/*
 * Copyright (c) 2019 The Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package htsjdk.samtools.cram.compression.rans;

final public class ArithmeticDecoder {
    public final int[] freq = new int[Constants.NUMBER_OF_SYMBOLS];
    public final int[] cumulativeFreq = new int[Constants.NUMBER_OF_SYMBOLS];

    // reverse lookup table
    public final byte[] reverseLookup = new byte[Constants.TOTAL_FREQ];

    public ArithmeticDecoder() {
        for (int i = 0; i < Constants.NUMBER_OF_SYMBOLS; i++) {
            freq[i] = 0;
            cumulativeFreq[i] = 0;
        }
    }

    public void reset() {
        for (int i = 0; i < Constants.NUMBER_OF_SYMBOLS; i++) {
            freq[i] = 0;
            cumulativeFreq[i] = 0;
        }
        for (int i = 0; i < Constants.TOTAL_FREQ; i++) {
            reverseLookup[i] = 0;
        }
    }

}