import React, { ReactNode } from 'react';
import * as Logic from 'logic';

/**
 * Converts an expression to a string for display purposes.
 * @param expr Some `Expression`
 * @returns A displayable string
 */
function exprToStr(expr: Logic.Expression): String {

  if (expr instanceof Logic.Expression.And) {
    return "(" + exprToStr(expr.lhs) + " and " + exprToStr(expr.rhs) + ")";

  } else if (expr instanceof Logic.Expression.Or) {
    return "(" + exprToStr(expr.lhs) + " or " + exprToStr(expr.rhs) + ")";

  } else if (expr == Logic.Expression.Top) {
    return "top";

  } else if (expr == Logic.Expression.Bottom) {
    return "bottom";

  } else if (expr instanceof Logic.Expression.Iff) {
    return "(" + exprToStr(expr.lhs) + " iff " + exprToStr(expr.rhs) + ")";

  } else if (expr instanceof Logic.Expression.Imp) {
    return "(" + exprToStr(expr.ant) + " imp " + exprToStr(expr.csq) + ")";

  } else if (expr instanceof Logic.Expression.Not) {
    return "(not " + exprToStr(expr.expr) + ")";

  } else if (expr instanceof Logic.Expression.Var) {
    return expr.id;

  }

  throw TypeError("Invalid expression");
}


interface Memo {
  [key: string]: number;
}
const memo: Memo = {};
/**
 * Counts the number of lines in a box.
 * @param box Some `Box`
 * @returns The number of lines in the box
 */
function countLines(box: Logic.Box): number {
  if (box.id in memo) return memo[box.id];

  return memo[box.id] = box.children.reduce((acc, proofPart) => {
    if (proofPart instanceof Logic.Line) {
      return acc + 1;
    } else {
      return acc + countLines(proofPart as Logic.Box);
    }
  }, 0);
}


interface ProofLineProps {
  line: Logic.Line
  lineNum: number
}
const ProofLine = ({ line, lineNum }: ProofLineProps): React.ReactNode => (
  <div>
    {lineNum} {exprToStr(line.expr)} [{line.infRule.tag}]
  </div>
);

/**
 * Convert the children of a `Logic.Box` to a list of displayable React elements.
 * @param proofs A list of Proof elements (either Line or Box)
 * @param currLineNum The line number of the first item in `proofs`
 * @returns A list of React elements
 */
function getBoxElements(proofs: Logic.Proof[], currLineNum: number): ReactNode[] {
  const components: React.ReactNode[] = []

  for (let childIndex = 0; childIndex < proofs.length; childIndex++) {

    const proofInBox = proofs[childIndex];

    // Create a line component if the box element is a line
    if (proofInBox instanceof Logic.Line) {
      components.push(<ProofLine line={proofInBox} lineNum={++currLineNum} />);
    
    // Create a box component if the box element is a box
    } else if (proofInBox instanceof Logic.Box) {

      // Handle adjacent boxes
      if (proofInBox.adj !== null) {
        childIndex++;
        const linesInFirstBox = countLines(proofInBox as Logic.Box);

        components.push(
          <div style={{display: "flex"}}>
            <ProofBox proof={proofInBox as Logic.Box} currLineNum={currLineNum} />
            <ProofBox proof={proofs[childIndex] as Logic.Box} currLineNum={currLineNum + linesInFirstBox} />
          </div>
        );

        currLineNum += linesInFirstBox + countLines(proofs[childIndex] as Logic.Box);

      // Handle standalone boxes
      } else {
        components.push(<ProofBox proof={proofInBox as Logic.Box} currLineNum={currLineNum} />);
        currLineNum += countLines(proofInBox as Logic.Box);
      }
    }
  }

  return components;
}


interface ProofBoxProps {
  proof: Logic.Box;
  currLineNum: number;
}
export default function ProofBox({ proof, currLineNum }: ProofBoxProps) {
  return (
    <div style={{ marginLeft: 30, border: "solid lightgray 1px", margin: 10, padding: 10 }}>
      {getBoxElements(proof.children, currLineNum)}
    </div>
  );
}
