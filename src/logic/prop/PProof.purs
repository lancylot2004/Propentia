module PProof where

import Data.Eq (class Eq)
import Data.List (List(..), (:), length)
import Data.Tuple (Tuple(..))
import Data.Unit (unit)
import PExpr (PExpr)
import Prelude ((-), (+), negate, (>=), (<))

type PProof = List PStep

data PStep = Parallel PProof PProof
           | Step PStepTag PExpr

data PStepTag = Prem | Ass       -- {Premise}, {Assumption}
              | Tick Int         -- Given (any redundant expression)
              | NotI Int Int     -- phi, bottom
              | NotE Int Int     -- not phi, phi
              | DNotI Int        -- Given (any expression)
              | DNotE Int        -- Given (a double negation)
              | AndI Int Int     -- LHS, RHS
              | AndE Int Int     -- LHS, RHS
              | OrI Int          -- Given (any expression)
              | ImpI Int Int     -- Antecedant, Consequent
              | ImpE Int Int     -- Implication, Antecedant
              | IffI Int Int     -- Imp ->, Imp <-
              | IffE Int Int     -- Iff <->, One of LHS or RHS
              | BotI Int Int     -- Expression, Contradiction
              | BotE Int         -- Bottom
              | TopI | TopE | EM -- {EM: Excluded Middle}
              | MT Int Int       -- {MT: Modus Tollens} phi -> psi, not psi
              | PC Int Int       -- {PC: by Contradiction} not phi, bottom
              | OrE Int (Tuple Int Int) (Tuple Int Int) -- Disjunct, LHS Proof, RHS Proof
-- TODO: Lemmas

derive instance eqPStepTag :: Eq PStepTag

-- TODO: Construct PStepTag as Functor to simplify code.
modLine :: PStepTag -> Int -> PStepTag
modLine (Tick l)    i = Tick (l + i)
modLine (NotI l l') i = NotI (l + i) (l' + i) 
modLine (NotE l l') i = NotE (l + i) (l' + i)
modLine (DNotI l)   i = DNotI (l + i)
modLine (DNotE l)   i = DNotE (l + i)
modLine (AndI l l') i = AndI (l + i) (l' + i)
modLine (AndE l l') i = AndE (l + i) (l' + i)
modLine (OrI l)     i = OrI (l + i)
modLine (ImpI l l') i = ImpI (l + i) (l' + i)
modLine (ImpE l l') i = ImpE (l + i) (l' + i)
modLine (IffI l l') i = IffI (l + i) (l' + i)
modLine (IffE l l') i = IffE (l + i) (l' + i)
modLine (BotI l l') i = BotI (l + i) (l' + i)
modLine (BotE l)    i = BotE (l + i)
modLine (MT l l')   i = MT (l + i) (l' + i)
modLine (PC l l')   i = PC (l + i) (l' + i)
modLine (OrE l (Tuple a a') (Tuple b b')) i
  = OrE (l - 1) (Tuple (a + i) (a' + i)) (Tuple (b + i) (b' + i))
modLine s           _ = s


delLine :: PProof -> Int -> PProof
-- If there is no more proof, but l != 0, what happened? (It's ok if l == 0)
delLine Nil 0                      = Nil
delLine Nil _                      = Nil -- TODO: Error for no more proof to consume.
delLine _ l | l < 0                = Nil -- TODO: Error for l < 0
-- If l == 0, propogate [-1] change to all subsequent line numbers..
delLine ((Step s e):ps) 0          = (Step (modLine s (-1)) e):delLine ps 0
delLine ((Parallel p p'):ps) 0     = (Parallel (delLine p 0) (delLine p' 0)):delLine ps 0
-- If l == 1, delete current line, and propagate changes.
delLine ((Step _ _):ps) 1          = delLine ps 0
delLine ((Parallel p p'):ps) 1     = (Parallel (delLine p 1) (delLine p' 0)):delLine ps 0
-- If l > 1, keep going.
delLine (p@(Step _ _):ps) l        = p:delLine ps (l - 1)
delLine (p@(Parallel p' p''):ps) l = 
  case unit of
    _ | lP'  >= l       -> (Parallel (delLine p' l) (delLine p'' 0)):delLine ps 0
    _ | lP'' >= l - lP' -> (Parallel p' (delLine p'' l)):delLine ps 0
    _                   -> p:delLine ps (l - 1)
  where
    lP'  = length p'
    lP'' = length p''

