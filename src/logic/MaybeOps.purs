module MaybeOps ( mNot, mAnd, mOr ) where

import Data.Maybe (Maybe(..))
import Prelude (not, (&&), (||))

-- Maybe (pun intended) one day I'll figure out how to infix these.

mNot :: Maybe Boolean -> Maybe Boolean
mNot (Just b) = Just (not b)
mNot Nothing  = Nothing

mAnd :: Maybe Boolean -> Maybe Boolean -> Maybe Boolean
mAnd (Just b) (Just b') = Just (b && b')
mAnd _ _                = Nothing

mOr :: Maybe Boolean -> Maybe Boolean -> Maybe Boolean
mOr (Just b) (Just b') = Just (b || b')
mOr (Just b) Nothing   = Just b
mOr Nothing (Just b)   = Just b
mOr _ _                = Nothing