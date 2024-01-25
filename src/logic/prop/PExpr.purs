module PExpr
  ( PExpr(..)
  , isAtomic, isClause, isLiteral
  , eval
  )
  where

import Prelude (otherwise, ($), (<<<), (==))
import Data.Foldable (all, lookup)
import Data.Tuple (Tuple)
import Data.Maybe (Maybe(..))
import MaybeOps (mNot, mAnd, mOr)

data PExpr = Var String | Top | Bottom
           | Not PExpr
           | And PExpr PExpr | Or PExpr PExpr
           | Imp PExpr PExpr | Iff PExpr PExpr

type Env = Array (Tuple String Boolean)

-- | Is a propositional atom, top, or bottom. (Excl. negated atomics.)
isAtomic :: PExpr -> Boolean
isAtomic Top     = true
isAtomic Bottom  = true
isAtomic (Var _) = true
isAtomic _       = false

-- | Is a literal (either atomic or negated-atomic).
isLiteral :: PExpr -> Boolean
isLiteral e
  | isAtomic e    = true
  | (Not e') <- e = isAtomic e'
  | otherwise     = false

-- | Is a clause (disjunction of one or more literals.)
isClause :: PExpr -> Boolean
isClause e
  | isLiteral e      = true
  | (Or e' e'') <- e = all isLiteral [e', e'']
  | otherwise        = false

-- | Evaluates a [PExpr] given an environment. Returns [Nothing] if there is insufficient knowledge.
eval :: Env -> PExpr -> Maybe Boolean
eval env (Var id)   = lookup id env
eval _    Top       = Just true
eval _    Bottom    = Just false
eval env (Not e)    = mNot (eval env e)
eval env (And e e') = mAnd (eval env e) (eval env e')
eval env (Or e e')  = mOr (eval env e) (eval env e')
eval env (Imp e e') = mOr (mNot <<< (eval env) $ e) (eval env e')
eval env (Iff e e') = Just $ eval env e == eval env e'