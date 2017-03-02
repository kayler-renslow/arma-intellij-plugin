/* The following code was generated by JFlex 1.7.0-SNAPSHOT tweaked for IntelliJ platform */

package com.kaylerrenslow.a3plugin.lang.sqf;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import java.util.Collections;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.7.0-SNAPSHOT
 * from the specification file <tt>SQF.flex</tt>
 */
class SQFLexer implements FlexLexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0, 0
  };

  /** 
   * Translates characters to character classes
   * Chosen bits are [12, 6, 3]
   * Total runtime size is 14848 bytes
   */
  public static int ZZ_CMAP(int ch) {
    return ZZ_CMAP_A[(ZZ_CMAP_Y[(ZZ_CMAP_Z[ch>>9]<<6)|((ch>>3)&0x3f)]<<3)|(ch&0x7)];
  }

  /* The ZZ_CMAP_Z table has 2176 entries */
  static final char ZZ_CMAP_Z[] = zzUnpackCMap(
    "\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15\1\16\1\17\1"+
    "\20\5\21\1\22\1\23\1\24\1\21\14\25\1\26\50\25\1\27\2\25\1\30\1\31\1\32\1\33"+
    "\25\25\1\34\20\21\1\35\1\36\1\37\1\40\1\41\1\42\1\43\1\21\1\44\1\45\1\46\1"+
    "\21\1\47\2\21\1\50\4\21\1\25\1\51\1\52\5\21\2\25\1\53\31\21\1\25\1\54\1\21"+
    "\1\55\40\21\1\56\17\21\1\57\1\60\1\61\1\62\13\21\1\63\10\21\123\25\1\64\7"+
    "\25\1\65\1\66\37\21\1\25\1\66\u0582\21\1\67\u017f\21");

  /* The ZZ_CMAP_Y table has 3584 entries */
  static final char ZZ_CMAP_Y[] = zzUnpackCMap(
    "\1\0\1\1\1\0\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15\1\16\1"+
    "\17\3\0\1\20\1\21\1\22\1\23\2\11\1\24\3\11\1\24\71\11\1\25\1\11\1\26\1\27"+
    "\1\30\1\31\2\27\16\0\1\32\1\20\1\33\1\34\2\11\1\35\11\11\1\36\21\11\1\37\1"+
    "\40\23\11\1\27\1\41\3\11\1\24\1\42\1\41\4\11\1\43\1\44\4\0\1\45\1\46\1\27"+
    "\3\11\2\47\1\27\1\50\1\51\1\0\1\52\5\11\1\53\3\0\1\54\1\55\13\11\1\56\1\45"+
    "\1\57\1\60\1\0\1\61\1\27\1\62\1\63\3\11\3\0\1\64\12\11\1\65\1\0\1\66\1\27"+
    "\1\0\1\67\3\11\1\53\1\70\1\23\2\11\1\65\1\71\1\72\1\73\2\27\3\11\1\74\10\27"+
    "\1\75\1\30\6\27\1\76\2\0\1\77\1\100\6\11\1\101\2\0\1\102\1\11\1\103\1\0\2"+
    "\41\1\104\1\105\1\106\2\11\1\75\1\107\1\110\1\111\1\112\1\62\1\113\1\103\1"+
    "\0\1\114\1\51\1\104\1\115\1\106\2\11\1\75\1\116\1\117\1\120\1\121\1\122\1"+
    "\123\1\124\1\0\1\125\1\27\1\104\1\36\1\35\2\11\1\75\1\126\1\110\1\45\1\127"+
    "\1\130\1\27\1\103\1\0\1\42\1\27\1\104\1\105\1\106\2\11\1\75\1\126\1\110\1"+
    "\111\1\121\1\124\1\113\1\103\1\0\1\42\1\27\1\131\1\132\1\133\1\134\1\135\1"+
    "\132\1\11\1\136\1\137\1\140\1\141\1\27\1\124\1\0\1\27\1\42\1\104\1\32\1\75"+
    "\2\11\1\75\1\142\1\143\1\144\1\140\1\145\1\26\1\103\1\0\2\27\1\146\1\32\1"+
    "\75\2\11\1\75\1\142\1\110\1\144\1\140\1\145\1\33\1\103\1\0\1\147\1\27\1\146"+
    "\1\32\1\75\4\11\1\150\1\144\1\151\1\62\1\27\1\103\1\0\1\27\1\40\1\146\1\11"+
    "\1\24\1\40\2\11\1\35\1\152\1\24\1\153\1\154\1\0\2\27\1\155\1\27\1\41\5\11"+
    "\1\156\1\157\1\160\1\77\1\0\1\161\4\27\1\162\1\163\1\164\1\41\1\165\1\166"+
    "\1\156\1\167\1\170\1\171\1\0\1\172\4\27\1\130\2\27\1\161\1\0\1\161\1\173\1"+
    "\174\1\11\1\41\3\11\1\30\1\44\1\0\1\144\1\175\1\0\1\44\3\0\1\50\1\176\7\27"+
    "\5\11\1\53\1\0\1\177\1\0\1\161\1\65\1\200\1\201\1\202\1\203\1\11\1\204\1\205"+
    "\1\0\1\171\4\11\1\36\1\22\5\11\1\206\51\11\1\133\1\24\1\133\5\11\1\133\4\11"+
    "\1\133\1\24\1\133\1\11\1\24\7\11\1\133\10\11\1\207\4\27\2\11\2\27\12\11\1"+
    "\30\1\27\1\41\114\11\1\105\2\11\1\41\2\11\1\47\11\11\1\132\1\130\1\27\1\11"+
    "\1\32\1\210\1\27\2\11\1\210\1\27\2\11\1\211\1\27\1\11\1\32\1\212\1\27\6\11"+
    "\1\213\3\0\1\214\1\215\1\0\1\161\3\27\1\216\1\0\1\161\13\11\1\27\5\11\1\217"+
    "\10\11\1\220\1\27\3\11\1\30\1\0\1\2\1\0\1\2\1\124\1\0\3\11\1\220\1\30\1\27"+
    "\5\11\1\114\2\0\1\55\1\161\1\0\1\161\4\27\2\11\1\160\1\2\6\11\1\175\1\77\3"+
    "\0\1\111\1\0\1\161\1\0\1\161\1\43\13\27\1\221\5\11\1\213\1\0\1\221\1\114\1"+
    "\0\1\161\1\27\1\222\1\2\1\27\1\223\3\11\1\102\1\202\1\0\1\67\4\11\1\65\1\0"+
    "\1\2\1\27\4\11\1\213\2\0\1\27\1\0\1\224\1\0\1\67\3\11\1\220\12\27\1\225\2"+
    "\0\1\226\1\227\1\27\30\11\4\0\1\77\2\27\1\76\42\11\2\220\4\11\2\220\1\11\1"+
    "\230\3\11\1\220\6\11\1\32\1\170\1\231\1\30\1\232\1\114\1\11\1\30\1\231\1\30"+
    "\1\27\1\222\3\27\1\233\1\27\1\43\1\130\1\27\1\234\1\27\1\50\1\235\1\42\1\43"+
    "\2\27\1\11\1\30\3\11\1\47\2\27\1\0\1\50\1\236\1\0\1\237\1\27\1\240\1\40\1"+
    "\152\1\241\1\31\1\242\1\11\1\243\1\244\1\245\2\27\5\11\1\130\116\27\5\11\1"+
    "\24\5\11\1\24\20\11\1\30\1\246\1\247\1\27\4\11\1\36\1\22\7\11\1\43\1\27\1"+
    "\62\2\11\1\24\1\27\10\24\4\0\5\27\1\43\72\27\1\244\3\27\1\41\1\204\1\241\1"+
    "\30\1\41\11\11\1\24\1\250\1\41\12\11\1\206\1\244\4\11\1\220\1\41\12\11\1\24"+
    "\2\27\3\11\1\47\6\27\170\11\1\220\11\27\71\11\1\30\6\27\21\11\1\30\10\27\5"+
    "\11\1\220\41\11\1\30\2\11\1\0\1\247\2\27\5\11\1\160\1\76\1\251\3\11\1\62\12"+
    "\11\1\161\3\27\1\43\1\11\1\40\14\11\1\252\1\114\1\27\1\11\1\47\11\27\1\11"+
    "\1\253\1\254\2\11\1\53\2\27\1\130\6\11\1\114\1\27\1\67\5\11\1\213\1\0\1\50"+
    "\1\27\1\0\1\161\2\0\1\67\1\51\1\0\1\67\2\11\1\65\1\171\2\11\1\160\1\0\1\2"+
    "\1\27\3\11\1\30\1\100\5\11\1\53\1\0\1\237\1\43\1\0\1\161\4\27\5\11\1\102\1"+
    "\77\1\27\1\254\1\255\1\0\1\161\2\11\1\24\1\256\6\11\1\201\1\257\1\217\2\27"+
    "\1\260\1\11\1\53\1\261\1\27\3\262\1\27\2\24\22\27\4\11\1\53\1\263\1\0\1\161"+
    "\64\11\1\114\1\27\2\11\1\24\1\264\5\11\1\114\40\27\55\11\1\220\15\11\1\26"+
    "\4\27\1\24\1\27\1\264\1\265\1\11\1\75\1\24\1\170\1\266\15\11\1\26\3\27\1\264"+
    "\54\11\1\220\2\27\10\11\1\40\6\11\5\27\1\11\1\30\2\0\2\27\1\77\1\27\1\135"+
    "\2\27\1\244\3\27\1\42\1\32\20\11\1\267\1\234\1\27\1\0\1\161\1\41\2\11\1\115"+
    "\1\41\2\11\1\47\1\270\12\11\1\24\3\40\1\271\1\272\2\27\1\273\1\11\1\142\2"+
    "\11\1\24\2\11\1\274\1\11\1\220\1\11\1\220\4\27\17\11\1\47\10\27\6\11\1\30"+
    "\20\27\1\275\20\27\3\11\1\30\6\11\1\130\5\27\3\11\1\24\2\27\3\11\1\47\6\27"+
    "\3\11\1\220\4\11\1\114\1\11\1\241\5\27\23\11\1\220\1\0\1\161\52\27\1\220\1"+
    "\75\4\11\1\36\1\276\2\11\1\220\25\27\2\11\1\220\1\27\3\11\1\26\10\27\7\11"+
    "\1\270\10\27\1\277\1\76\1\142\1\41\2\11\1\114\1\120\4\27\3\11\1\30\20\27\6"+
    "\11\1\220\1\27\2\11\1\220\1\27\2\11\1\47\21\27\11\11\1\130\66\27\1\223\6\11"+
    "\1\0\1\77\3\27\1\124\1\0\2\27\1\223\5\11\1\0\1\300\2\27\3\11\1\130\1\0\1\161"+
    "\1\223\3\11\1\160\1\0\1\144\1\0\10\27\1\223\5\11\1\53\1\0\1\301\1\27\1\0\1"+
    "\161\24\27\5\11\1\53\1\0\1\27\1\0\1\161\46\27\55\11\1\24\22\27\14\11\1\47"+
    "\63\27\5\11\1\24\72\27\7\11\1\130\130\27\10\11\1\30\1\27\1\102\4\0\1\77\1"+
    "\27\1\62\1\223\1\11\14\27\1\26\153\27\1\302\1\303\2\0\1\304\1\2\3\27\1\305"+
    "\22\27\1\306\67\27\12\11\1\32\10\11\1\32\1\307\1\310\1\11\1\311\1\142\7\11"+
    "\1\36\1\312\2\32\3\11\1\313\1\170\1\40\1\75\51\11\1\220\3\11\1\75\2\11\1\206"+
    "\3\11\1\206\2\11\1\32\3\11\1\32\2\11\1\24\3\11\1\24\3\11\1\75\3\11\1\75\2"+
    "\11\1\206\1\314\6\0\1\142\3\11\1\162\1\41\1\206\1\315\1\240\1\316\1\162\1"+
    "\230\1\162\2\206\1\123\1\11\1\35\1\11\1\114\1\317\1\35\1\11\1\114\50\27\32"+
    "\11\1\24\5\27\106\11\1\30\1\27\33\11\1\220\74\27\1\122\3\27\14\0\20\27\36"+
    "\0\2\27");

  /* The ZZ_CMAP_A table has 1664 entries */
  static final char ZZ_CMAP_A[] = zzUnpackCMap(
    "\11\2\1\6\1\5\1\70\1\6\1\4\6\2\4\0\1\6\1\46\1\16\1\23\1\3\1\53\1\51\1\17\1"+
    "\56\1\57\1\21\1\54\1\64\1\12\1\10\1\20\1\13\11\7\1\67\1\65\1\50\1\45\1\47"+
    "\1\66\1\0\4\15\1\11\1\15\2\3\1\43\16\3\1\14\2\3\1\62\1\22\1\63\1\55\1\1\1"+
    "\0\1\41\1\15\1\42\1\24\1\25\1\26\1\3\1\35\1\27\2\3\1\32\1\3\1\30\1\37\1\44"+
    "\1\3\1\40\1\33\1\34\1\31\2\3\1\36\2\3\1\60\1\52\1\61\1\0\6\2\1\71\2\2\2\0"+
    "\4\3\4\0\1\3\2\0\1\2\7\0\1\3\4\0\1\3\5\0\7\3\1\0\2\3\4\0\4\3\16\0\5\3\7\0"+
    "\1\3\1\0\1\3\1\0\5\3\1\0\2\3\6\0\1\3\1\0\3\3\1\0\1\3\1\0\4\3\1\0\13\3\1\0"+
    "\3\3\1\0\5\2\2\0\6\3\1\0\7\3\1\0\1\3\15\0\1\3\1\0\15\2\1\0\1\2\1\0\2\2\1\0"+
    "\2\2\1\0\1\2\3\3\5\0\5\2\6\0\1\3\4\0\3\2\5\0\3\3\7\2\4\0\2\3\1\2\13\3\1\0"+
    "\1\3\7\2\2\3\2\2\1\0\4\2\2\3\2\2\3\3\2\0\1\3\7\0\1\2\1\3\1\2\6\3\3\2\2\0\11"+
    "\3\3\2\1\3\6\0\2\2\6\3\4\2\2\3\2\0\2\2\1\3\11\2\1\3\3\2\1\3\5\2\2\0\1\3\3"+
    "\2\4\0\1\3\1\0\6\3\4\0\13\2\1\0\4\2\6\3\3\2\1\3\2\2\1\3\7\2\2\3\2\2\2\0\2"+
    "\2\1\0\3\2\1\0\10\3\2\0\2\3\2\0\6\3\1\0\1\3\3\0\4\3\2\0\1\2\1\3\7\2\2\0\2"+
    "\2\2\0\3\2\1\3\5\0\2\3\1\0\5\3\4\0\3\3\4\0\2\3\1\0\2\3\1\0\2\3\1\0\2\3\2\0"+
    "\1\2\1\0\5\2\4\0\2\2\2\0\3\2\3\0\1\2\7\0\4\3\1\0\1\3\7\0\4\2\3\3\1\2\2\0\1"+
    "\3\1\0\2\3\1\0\3\3\2\2\1\0\3\2\2\0\1\3\11\0\1\2\1\3\1\0\6\3\3\0\3\3\1\0\4"+
    "\3\3\0\2\3\1\0\1\3\1\0\2\3\3\0\2\3\3\0\2\3\4\0\5\2\3\0\3\2\1\0\4\2\2\0\1\3"+
    "\6\0\1\2\4\3\1\0\5\3\3\0\1\3\7\2\1\0\2\2\5\0\2\2\3\0\2\2\1\0\3\3\1\0\2\3\5"+
    "\0\3\3\2\0\1\3\3\2\1\0\4\2\1\3\1\0\4\3\1\0\1\3\4\0\1\2\4\0\6\2\1\0\1\2\3\0"+
    "\2\2\4\0\1\3\1\2\2\3\7\2\4\0\10\3\3\2\7\0\2\3\1\0\1\3\2\0\2\3\1\0\1\3\2\0"+
    "\1\3\6\0\4\3\1\0\3\3\1\0\1\3\1\0\1\3\2\0\2\3\1\0\3\3\2\2\1\0\2\2\1\3\2\0\5"+
    "\3\1\0\1\3\1\0\6\2\2\0\2\2\2\0\4\3\5\0\1\2\1\0\1\2\1\0\1\2\4\0\2\2\5\3\3\2"+
    "\6\0\1\2\1\0\7\2\1\3\2\2\4\3\3\2\1\3\3\2\2\3\7\2\3\3\4\2\5\3\14\2\1\3\1\2"+
    "\3\3\1\0\7\3\2\0\3\2\2\3\3\2\3\0\2\3\2\2\4\0\1\3\1\0\2\2\4\0\4\3\10\2\3\0"+
    "\1\3\3\0\2\3\1\2\5\0\3\2\2\0\1\3\1\2\1\3\5\0\6\3\2\0\5\2\3\3\3\0\10\2\5\3"+
    "\2\2\3\0\3\3\3\2\1\0\5\2\4\3\1\2\4\3\3\2\2\3\2\0\1\3\1\0\1\3\1\0\1\3\1\0\1"+
    "\3\2\0\3\3\1\0\6\3\2\0\2\3\2\70\5\2\5\0\1\3\5\0\6\2\1\0\1\2\3\0\4\2\11\0\1"+
    "\3\4\0\1\3\1\0\5\3\2\0\1\3\1\0\4\3\1\0\3\3\2\0\4\3\5\0\5\3\4\0\1\3\4\0\4\3"+
    "\3\2\2\3\5\0\2\2\2\0\3\3\6\2\1\0\2\3\2\0\4\3\1\0\2\3\1\2\3\3\1\2\4\3\1\2\10"+
    "\3\2\2\4\0\1\3\1\2\4\0\1\2\5\3\2\2\3\0\3\3\4\0\3\3\2\2\2\0\6\3\1\0\3\2\1\0"+
    "\2\2\5\0\5\3\5\0\1\3\1\2\3\3\1\0\2\3\1\0\7\3\2\0\1\2\6\0\2\3\2\0\3\3\3\0\2"+
    "\3\3\0\2\3\2\0\3\2\4\0\3\3\1\0\2\3\1\0\1\3\5\0\1\2\2\0\1\3\3\0\1\3\2\0\2\3"+
    "\3\2\1\0\2\2\1\0\3\2\2\0\1\2\2\0\1\2\4\3\10\0\5\2\3\0\6\2\2\0\3\2\2\0\4\2"+
    "\4\0\3\2\5\0\1\3\2\0\2\3\2\0\4\3\1\0\4\3\1\0\1\3\1\0\6\3\2\0\5\3\1\0\4\3\1"+
    "\0\4\3\2\0\2\2\1\0\1\3\1\0\1\3\5\0\1\3\1\0\1\3\1\0\3\3\1\0\3\3\1\0\3\3");

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\1\2\1\3\1\4\1\5\1\1\1\6"+
    "\1\5\2\1\1\7\1\10\1\1\2\3\1\11\1\12"+
    "\1\13\1\14\2\1\1\15\1\16\1\17\1\20\1\21"+
    "\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31"+
    "\4\2\1\32\1\0\2\33\2\0\1\34\1\0\1\34"+
    "\1\35\5\0\2\3\1\36\1\37\1\40\1\41\1\42"+
    "\1\43\1\44\3\2\1\33\2\45\6\0\2\3\3\2"+
    "\1\45\1\46\4\0\1\32\1\47\2\2\1\45\2\0"+
    "\1\4\2\2\1\45\2\4\2\2\1\45\2\2\1\45"+
    "\2\2\1\45\1\2\1\45\2\2";

  private static int [] zzUnpackAction() {
    int [] result = new int[112];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\72\0\164\0\256\0\350\0\u0122\0\u015c\0\72"+
    "\0\u0196\0\u01d0\0\u020a\0\u0244\0\72\0\u027e\0\u02b8\0\u02f2"+
    "\0\u032c\0\u0366\0\u03a0\0\u03da\0\u0414\0\u044e\0\72\0\72"+
    "\0\72\0\72\0\72\0\72\0\72\0\72\0\72\0\72"+
    "\0\72\0\72\0\72\0\u0488\0\u04c2\0\u04fc\0\u0536\0\u0488"+
    "\0\u015c\0\u0570\0\u05aa\0\u05e4\0\u01d0\0\u061e\0\u020a\0\u0658"+
    "\0\u0692\0\u06cc\0\u0706\0\u0740\0\u077a\0\u07b4\0\u07ee\0\u0828"+
    "\0\72\0\72\0\72\0\72\0\72\0\72\0\72\0\u0862"+
    "\0\u089c\0\u08d6\0\u0910\0\u094a\0\u05e4\0\u0984\0\u09be\0\u09f8"+
    "\0\u0a32\0\u0a6c\0\u0aa6\0\u0ae0\0\u0b1a\0\u0b54\0\u0b8e\0\u0bc8"+
    "\0\u0c02\0\72\0\u0c3c\0\u0c76\0\u0cb0\0\u0cea\0\256\0\256"+
    "\0\u0d24\0\u0d5e\0\u0d98\0\u0dd2\0\u0e0c\0\u0e46\0\u0e80\0\u0eba"+
    "\0\u0ef4\0\u0f2e\0\u0f68\0\u0fa2\0\u0fdc\0\u1016\0\u1050\0\u108a"+
    "\0\u10c4\0\u10fe\0\u1138\0\u1172\0\u11ac\0\72\0\u11e6\0\u1220";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[112];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\2\1\3\1\2\1\4\3\5\1\6\1\7\1\4"+
    "\1\10\1\11\2\4\1\12\1\13\1\14\1\15\1\2"+
    "\1\16\10\4\1\17\5\4\1\20\2\4\1\21\1\22"+
    "\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32"+
    "\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42"+
    "\1\43\75\0\3\44\3\0\1\44\1\0\1\44\1\0"+
    "\3\44\6\0\1\44\1\45\1\46\5\44\1\47\1\44"+
    "\1\50\6\44\24\0\1\44\1\0\3\4\3\0\1\4"+
    "\1\0\1\4\1\0\3\4\6\0\21\4\24\0\1\4"+
    "\4\0\3\5\72\0\1\6\1\51\1\52\1\0\1\6"+
    "\11\0\1\52\53\0\1\53\3\0\1\53\65\0\1\6"+
    "\1\51\1\52\1\0\1\6\1\54\10\0\1\52\10\0"+
    "\1\54\33\0\16\55\1\56\53\55\17\57\1\60\52\57"+
    "\20\0\1\61\1\62\74\0\1\63\1\64\1\0\1\65"+
    "\1\0\1\66\41\0\3\4\3\0\1\4\1\0\1\4"+
    "\1\0\3\4\6\0\11\4\1\67\7\4\24\0\1\4"+
    "\1\0\3\4\3\0\1\4\1\0\1\4\1\0\3\4"+
    "\6\0\15\4\1\70\3\4\24\0\1\4\45\0\1\71"+
    "\71\0\1\72\71\0\1\73\1\0\1\74\67\0\1\75"+
    "\75\0\1\76\72\0\1\77\20\0\3\44\3\0\1\44"+
    "\1\0\1\44\1\0\3\44\6\0\21\44\24\0\1\44"+
    "\1\0\3\44\3\0\1\44\1\0\1\44\1\0\3\44"+
    "\6\0\12\44\1\100\6\44\24\0\1\44\1\0\3\44"+
    "\3\0\1\44\1\0\1\44\1\0\3\44\6\0\13\44"+
    "\1\101\5\44\24\0\1\44\1\0\3\44\3\0\1\44"+
    "\1\0\1\44\1\0\3\44\6\0\11\44\1\102\7\44"+
    "\24\0\1\44\7\0\1\103\2\0\2\103\40\0\1\103"+
    "\24\0\1\53\1\0\1\52\1\0\1\53\11\0\1\52"+
    "\53\0\1\104\1\0\1\104\1\0\1\105\1\0\1\104"+
    "\6\0\3\104\12\0\2\104\45\0\1\55\72\0\1\57"+
    "\52\0\4\61\2\0\64\61\21\62\1\106\50\62\25\0"+
    "\1\107\74\0\1\110\1\0\1\111\65\0\1\112\73\0"+
    "\1\113\42\0\3\4\3\0\1\4\1\0\1\4\1\0"+
    "\3\4\6\0\3\4\1\114\15\4\24\0\1\4\1\0"+
    "\3\4\3\0\1\4\1\0\1\4\1\0\3\4\6\0"+
    "\7\4\1\115\11\4\24\0\1\4\1\0\3\44\3\0"+
    "\1\44\1\0\1\44\1\0\3\44\6\0\16\44\1\116"+
    "\2\44\24\0\1\44\1\0\3\44\3\0\1\44\1\0"+
    "\1\44\1\0\3\44\6\0\14\44\1\117\4\44\24\0"+
    "\1\44\1\0\3\44\3\0\1\44\1\0\1\44\1\0"+
    "\3\44\6\0\3\44\1\120\15\44\24\0\1\44\7\0"+
    "\1\103\3\0\1\103\65\0\1\121\1\0\1\121\1\0"+
    "\1\121\1\0\1\121\6\0\3\121\12\0\2\121\27\0"+
    "\20\62\1\122\1\106\50\62\26\0\1\123\67\0\1\124"+
    "\100\0\1\125\62\0\1\126\3\0\1\113\65\0\1\126"+
    "\46\0\3\4\3\0\1\4\1\0\1\4\1\0\3\4"+
    "\6\0\7\4\1\127\11\4\24\0\1\4\1\0\3\4"+
    "\3\0\1\4\1\0\1\4\1\0\3\4\6\0\1\4"+
    "\1\130\17\4\24\0\1\4\1\0\3\44\3\0\1\44"+
    "\1\0\1\44\1\0\3\44\6\0\1\44\1\131\17\44"+
    "\24\0\1\44\1\0\3\44\3\0\1\44\1\0\1\132"+
    "\1\0\3\44\6\0\21\44\24\0\1\44\1\0\3\44"+
    "\3\0\1\44\1\0\1\44\1\0\3\44\6\0\7\44"+
    "\1\50\11\44\24\0\1\44\7\0\1\133\1\0\1\133"+
    "\1\0\1\133\1\0\1\133\6\0\3\133\12\0\2\133"+
    "\56\0\1\134\71\0\1\135\67\0\1\136\71\0\1\135"+
    "\45\0\3\44\3\0\1\44\1\0\1\44\1\0\3\44"+
    "\6\0\20\44\1\137\24\0\1\44\1\0\3\44\3\0"+
    "\1\44\1\0\1\44\1\0\3\44\6\0\15\44\1\140"+
    "\3\44\24\0\1\44\7\0\1\141\1\0\1\141\1\0"+
    "\1\141\1\0\1\141\6\0\3\141\12\0\2\141\57\0"+
    "\1\125\67\0\1\136\43\0\4\142\1\143\15\142\1\136"+
    "\47\142\1\0\3\44\3\0\1\44\1\0\1\44\1\0"+
    "\3\44\6\0\10\44\1\144\10\44\24\0\1\44\1\0"+
    "\3\44\3\0\1\44\1\0\1\44\1\0\3\44\6\0"+
    "\16\44\1\145\2\44\24\0\1\44\7\0\1\146\1\0"+
    "\1\146\1\0\1\146\1\0\1\146\6\0\3\146\12\0"+
    "\2\146\27\0\4\142\2\0\14\142\1\136\53\142\1\0"+
    "\15\142\1\136\47\142\1\0\3\44\3\0\1\44\1\0"+
    "\1\44\1\0\3\44\6\0\3\44\1\147\15\44\24\0"+
    "\1\44\1\0\3\44\3\0\1\44\1\0\1\44\1\0"+
    "\3\44\6\0\11\44\1\150\7\44\24\0\1\44\7\0"+
    "\1\151\1\0\1\151\1\0\1\151\1\0\1\151\6\0"+
    "\3\151\12\0\2\151\30\0\3\44\3\0\1\44\1\0"+
    "\1\44\1\0\3\44\6\0\13\44\1\152\5\44\24\0"+
    "\1\44\1\0\3\44\3\0\1\44\1\0\1\44\1\0"+
    "\3\44\6\0\17\44\1\153\1\44\24\0\1\44\7\0"+
    "\1\154\1\0\1\154\1\0\1\154\1\0\1\154\6\0"+
    "\3\154\12\0\2\154\30\0\3\44\3\0\1\44\1\0"+
    "\1\44\1\0\3\44\6\0\4\44\1\50\14\44\24\0"+
    "\1\44\1\0\3\44\3\0\1\44\1\0\1\44\1\0"+
    "\3\44\6\0\4\44\1\155\14\44\24\0\1\44\7\0"+
    "\1\156\1\0\1\156\1\0\1\156\1\0\1\156\6\0"+
    "\3\156\12\0\2\156\30\0\3\44\3\0\1\44\1\0"+
    "\1\44\1\0\3\44\6\0\1\157\20\44\24\0\1\44"+
    "\1\0\3\44\3\0\1\44\1\0\1\44\1\0\3\44"+
    "\6\0\1\44\1\160\17\44\24\0\1\44\1\0\3\44"+
    "\3\0\1\44\1\0\1\44\1\0\3\44\6\0\12\44"+
    "\1\50\6\44\24\0\1\44";

  private static int [] zzUnpackTrans() {
    int [] result = new int[4698];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String[] ZZ_ERROR_MSG = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\11\5\1\1\11\4\1\1\11\11\1\15\11"+
    "\5\1\1\0\2\1\2\0\1\1\1\0\2\1\5\0"+
    "\2\1\7\11\6\1\6\0\6\1\1\11\4\0\5\1"+
    "\2\0\20\1\1\11\2\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[112];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private CharSequence zzBuffer = "";

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /**
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  SQFLexer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    int size = 0;
    for (int i = 0, length = packed.length(); i < length; i += 2) {
      size += packed.charAt(i);
    }
    char[] map = new char[size];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < packed.length()) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }

  public final int getTokenStart() {
    return zzStartRead;
  }

  public final int getTokenEnd() {
    return getTokenStart() + yylength();
  }

  public void reset(CharSequence buffer, int start, int end, int initialState) {
    zzBuffer = buffer;
    zzCurrentPos = zzMarkedPos = zzStartRead = start;
    zzAtEOF  = false;
    zzAtBOL = true;
    zzEndRead = end;
    yybegin(initialState);
  }

  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   *
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {
    return true;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final CharSequence yytext() {
    return zzBuffer.subSequence(zzStartRead, zzMarkedPos);
  }


  /**
   * Returns the character at position <tt>pos</tt> from the
   * matched text.
   *
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch.
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer.charAt(zzStartRead+pos);
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of
   * yypushback(int) and a match-all fallback rule) this method
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() {
    if (!zzEOFDone) {
      zzEOFDone = true;
        return;

    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public IElementType advance() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    CharSequence zzBufferL = zzBuffer;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL/*, zzEndReadL*/);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL/*, zzEndReadL*/);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + ZZ_CMAP(zzInput) ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
        zzDoEOF();
        return null;
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1: 
            { return TokenType.BAD_CHARACTER;
            }
          case 40: break;
          case 2: 
            { return SQFTypes.LOCAL_VAR;
            }
          case 41: break;
          case 3: 
            { for(String command : SQFStatic.LIST_COMMANDS){  //don't use binary search so that we can do ignore case search
        if(command.equalsIgnoreCase(yytext().toString())){
            return SQFTypes.COMMAND_TOKEN;
        }
    }
    return SQFTypes.GLOBAL_VAR;
            }
          case 42: break;
          case 4: 
            { return TokenType.WHITE_SPACE;
            }
          case 43: break;
          case 5: 
            { return SQFTypes.INTEGER_LITERAL;
            }
          case 44: break;
          case 6: 
            { return SQFTypes.MINUS;
            }
          case 45: break;
          case 7: 
            { return SQFTypes.FSLASH;
            }
          case 46: break;
          case 8: 
            { return SQFTypes.ASTERISK;
            }
          case 47: break;
          case 9: 
            { return SQFTypes.EQ;
            }
          case 48: break;
          case 10: 
            { return SQFTypes.EXCL;
            }
          case 49: break;
          case 11: 
            { return SQFTypes.GT;
            }
          case 50: break;
          case 12: 
            { return SQFTypes.LT;
            }
          case 51: break;
          case 13: 
            { return SQFTypes.PERC;
            }
          case 52: break;
          case 14: 
            { return SQFTypes.PLUS;
            }
          case 53: break;
          case 15: 
            { return SQFTypes.CARET;
            }
          case 54: break;
          case 16: 
            { return SQFTypes.LPAREN;
            }
          case 55: break;
          case 17: 
            { return SQFTypes.RPAREN;
            }
          case 56: break;
          case 18: 
            { return SQFTypes.LBRACE;
            }
          case 57: break;
          case 19: 
            { return SQFTypes.RBRACE;
            }
          case 58: break;
          case 20: 
            { return SQFTypes.LBRACKET;
            }
          case 59: break;
          case 21: 
            { return SQFTypes.RBRACKET;
            }
          case 60: break;
          case 22: 
            { return SQFTypes.COMMA;
            }
          case 61: break;
          case 23: 
            { return SQFTypes.SEMICOLON;
            }
          case 62: break;
          case 24: 
            { return SQFTypes.QUEST;
            }
          case 63: break;
          case 25: 
            { return SQFTypes.COLON;
            }
          case 64: break;
          case 26: 
            { return SQFTypes.LANG_VAR;
            }
          case 65: break;
          case 27: 
            { return SQFTypes.DEC_LITERAL;
            }
          case 66: break;
          case 28: 
            { return SQFTypes.STRING_LITERAL;
            }
          case 67: break;
          case 29: 
            { return SQFTypes.INLINE_COMMENT;
            }
          case 68: break;
          case 30: 
            { return SQFTypes.EQEQ;
            }
          case 69: break;
          case 31: 
            { return SQFTypes.NE;
            }
          case 70: break;
          case 32: 
            { return SQFTypes.GE;
            }
          case 71: break;
          case 33: 
            { return SQFTypes.GTGT;
            }
          case 72: break;
          case 34: 
            { return SQFTypes.LE;
            }
          case 73: break;
          case 35: 
            { return SQFTypes.AMPAMP;
            }
          case 74: break;
          case 36: 
            { return SQFTypes.BARBAR;
            }
          case 75: break;
          case 37: 
            { return SQFTypes.HEX_LITERAL;
            }
          case 76: break;
          case 38: 
            { return SQFTypes.BLOCK_COMMENT;
            }
          case 77: break;
          case 39: 
            { return SQFTypes.CASE;
            }
          case 78: break;
          default:
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
