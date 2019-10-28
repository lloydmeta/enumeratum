(define-struct inventory (blocks wood sheep wheat rocks))
;; An Inventory is a (make-inventory Nat Nat Nat Nat Nat)
;;     requires: wheat >= sheep
;;               wheat >= 4

(define-struct cost (blocks wood sheep wheat rocks))
;; A Cost is a (make-cost Nat Nat Nat Nat Nat)

(define (cost-template cost)
  (... (cost-blocks cost) ...
       (cost-wood cost) ...
       (cost-sheep cost) ...
       (cost-wheat cost) ...
       (cost-rocks cost) ...))
;;
;; ***************************************************
;;   Quan Cheng Taian (20835721)
;;   CS 135 Fall 2019
;;   Assignment 06, Problem 1 (a)
;; ***************************************************
;;



;; inventory-template: Inventory -> Any
(define (inventory-template inventory)
  (... (inventory-blocks inventory) ...
       (inventory-wood inventory) ...
       (inventory-sheep inventory) ...
       (inventory-wheat inventory) ...
       (inventory-rocks inventory) ...))

;;
;; ***************************************************
;;   Quan Cheng Taian (20835721)
;;   CS 135 Fall 2019
;;   Assignment 06, Problem 1 (b)
;; ***************************************************
;;

;; (valid-inventory? value) consumes a value and produces true
;;      if it is a valid inventory
;; valid-inventory?: Any -> Bool
;; Examples:

(check-expect (valid-inventory? (make-inventory 3 2 4 4 1)) true)
(check-expect (valid-inventory? (make-inventory 3 37 4 4 12)) true)


(define (valid-inventory? value)
  (and (inventory? value)
       (integer? (inventory-blocks value)) (<= 0 (inventory-blocks value))
       (integer? (inventory-wood value)) (<= 0 (inventory-wood value))
       (integer? (inventory-sheep value)) (<= 0 (inventory-sheep value))
       (integer? (inventory-wheat value)) (<= 0 (inventory-wheat value))
       (integer? (inventory-rocks value)) (<= 0 (inventory-rocks value))
       (>= (inventory-wheat value) (inventory-sheep value))
       (>= (inventory-wheat value) 4)))
;; Tests
(check-expect (valid-inventory? (make-inventory 0 0 4 3 0)) false)
(check-expect (valid-inventory? (make-inventory 0 0 0 0 0)) false)
(check-expect (valid-inventory? 'inventory) false)
(check-expect (valid-inventory? (make-inventory 1 8 4 5 7)) true)
(check-expect (valid-inventory? (make-inventory 2 7 3 4 4)) true)
(check-expect (valid-inventory? (make-inventory 7 4 3 3 6)) false)
(check-expect (valid-inventory? (make-inventory 3 10 4 3 9)) false)
(check-expect (valid-inventory? (make-inventory 3 10 4 46 24)) true)
(check-expect (valid-inventory? (make-inventory -3 10 4 46 24)) false)
(check-expect (valid-inventory? (make-inventory -3 -7 4 46 24)) false)

;;
;; ***************************************************
;;   Quan Cheng Taian (20835721)
;;   CS 135 Fall 2019
;;   Assignment 06, Problem 1 (c)
;; ***************************************************
;;

;; (affordable? inventory cost) consumes an Inventory and a Cost and produces
;;       true if the cost is covered by the inventory and if the inventory
;;       remains a valid inventory after subtracting the cost and false
;;       otherwise.
;; affordable?: Inventory Cost -> Bool
;; Examples:
(check-expect (affordable? (make-inventory 3 6 7 8 9)
                           (make-cost 2 4 5 8 9)) false)
(check-expect (affordable? (make-inventory 3 6 7 12 9)
                           (make-cost 2 4 5 8 9)) true)
(check-expect (affordable? (make-inventory 3 6 7 8 9)
                           (make-cost 2 4 5 9 9)) false)

(define (affordable? inventory cost)
  (valid-inventory?
   (make-inventory
    (- (inventory-blocks inventory) (cost-blocks cost))
    (- (inventory-wood inventory) (cost-wood cost))
    (- (inventory-sheep inventory) (cost-sheep cost))
    (- (inventory-wheat inventory) (cost-wheat cost))
    (- (inventory-rocks inventory) (cost-rocks cost)))))
;; Tests
(check-expect (affordable? (make-inventory 1 7 7 7 9)
                           (make-cost 2 4 5 9 9)) false)
(check-expect (affordable? (make-inventory 1 7 7 7 7)
                           (make-cost 2 4 5 9 9)) false)
(check-expect (affordable? (make-inventory 2 3 7 7 7)
                           (make-cost 2 4 5 9 9)) false)
(check-expect (affordable? (make-inventory 1 3 9 7 7)
                           (make-cost 2 4 5 9 9)) false)
(check-expect (affordable? (make-inventory 1 3 4 9 7)
                           (make-cost 2 4 5 9 9)) false)
(check-expect (affordable? (make-inventory 1 3 4 7 10)
                           (make-cost 2 4 5 9 9)) false)
;; check that wheat >= sheep and wheat >= 4
(check-expect (affordable? (make-inventory 7 7 9 10 12)
                           (make-cost 2 4 5 9 9)) false)
(check-expect (affordable? (make-inventory 7 7 9 13 12)
                           (make-cost 2 4 5 9 9)) true)

;;
;; ***************************************************
;;   Quan Cheng Taian (20835721)
;;   CS 135 Fall 2019
;;   Assignment 06, Problem 1 (d)
;; ***************************************************
;;

(define inventory-blocks-item-number 1)
(define inventory-wood-item-number 2)
(define inventory-sheep-item-number 3)
(define inventory-wheat-item-number 4)
(define inventory-rocks-item-number 5)

;; helper function: (finalize purpose later) (also come UP WITH A BETTER NAME!)
;;      (can-take? inventory item-number) consumes an Inventory inventory and
;;      an item-number item-number and returns -1 if the thief can take one
;;      unit of the item corresponding to that item number and 0 otherwise.
;; can-take?: Inventory Nat -> (any of -1 0)
;; Example
(check-expect (can-take (make-inventory 3 2 4 6 7)
                         (inventory-blocks (make-inventory 3 2 4 6 7))) -1)

(define (can-take inventory item-number)
  (cond [(and (= item-number inventory-blocks-item-number)
              (affordable? inventory (make-cost 1 0 0 0 0))) -1]
        
        [(and (= item-number inventory-wood-item-number)
              (affordable? inventory (make-cost 0 1 0 0 0))) -1]
        
        [(and (= item-number inventory-sheep-item-number)
              (affordable? inventory (make-cost 0 0 1 0 0))) -1]
        
        [(and (= item-number inventory-wheat-item-number)
              (or (affordable? inventory (make-cost 0 0 0 1 0))
                  (and (>= (inventory-wheat inventory) 5)
                       (= (inventory-wheat inventory)
                          (inventory-sheep inventory))))) -1]
        
        [(and (= item-number inventory-rocks-item-number)
              (affordable? inventory (make-cost 0 0 0 0 1))) -1]
        
        [else 0]))
;; tests
(check-expect (can-take (make-inventory 3 2 7 7 7)
                        (inventory-blocks (make-inventory 3 2 7 7 7))) -1)
(check-expect (can-take (make-inventory 3 2 7 7 7)
                        (inventory-wheat (make-inventory 3 2 7 7 7))) 0)

;; (thief inventory) consumes an Inventory inventory and produces another
;;      Inventory. The thief attempts to steal one unit of each resource. If
;;      this is not possible, the thief leaves this one resource unchanged.
;;      Calling (valid-inventory? (thief ...)) on any Inventory will always
;;      produce true.
;; thief: Inventory -> Inventory
;; Examples
(check-expect (thief (make-inventory 3 2 4 6 7)) (make-inventory 2 1 3 5 6))
(check-expect (thief (make-inventory 3 2 4 4 7)) (make-inventory 2 1 3 4 6))

(define (thief inventory)
  (make-inventory (+ (inventory-blocks inventory)
                     (can-take inventory inventory-blocks-item-number))
                  
                  (+ (inventory-wood inventory)
                     (can-take inventory inventory-wood-item-number))
                  
                  (+ (inventory-sheep inventory)
                     (can-take inventory inventory-sheep-item-number))
                  
                  (+ (inventory-wheat inventory)
                     (can-take inventory inventory-wheat-item-number))
                  
                  (+ (inventory-rocks inventory)
                     (can-take inventory inventory-rocks-item-number))))

;; Tests (ordered based on the number of items that can be taken)
;; 1 item
(check-expect (thief (make-inventory 0 0 0 4 7)) (make-inventory 0 0 0 4 6))
(check-expect (thief (make-inventory 0 1 0 4 0)) (make-inventory 0 0 0 4 0))
;; 2 items
(check-expect (thief (make-inventory 0 0 0 5 0)) (make-inventory 0 0 0 4 0))
(check-expect (thief (make-inventory 0 1 0 4 7)) (make-inventory 0 0 0 4 6))
(check-expect (thief (make-inventory 0 2 0 5 0)) (make-inventory 0 1 0 4 0))
;; 3 items
(check-expect (thief (make-inventory 2 3 1 4 0)) (make-inventory 1 2 0 4 0))
(check-expect (thief (make-inventory 2 3 4 4 0)) (make-inventory 1 2 3 4 0))
(check-expect (thief (make-inventory 2 0 6 6 0)) (make-inventory 1 0 5 5 0))
;; 4 items
(check-expect (thief (make-inventory 2 3 5 5 0)) (make-inventory 1 2 4 4 0))
(check-expect (thief (make-inventory 3 3 4 4 7)) (make-inventory 2 2 3 4 6))
(check-expect (thief (make-inventory 3 3 0 5 7)) (make-inventory 2 2 0 4 6))
;; 5 items
(check-expect (thief (make-inventory 3 2 5 5 7)) (make-inventory 2 1 4 4 6))
(check-expect (thief (make-inventory 3 2 9 12 7)) (make-inventory 2 1 8 11 6))
(check-expect (thief (make-inventory 3 2 4 20 7)) (make-inventory 2 1 3 19 6))
(check-expect (thief (make-inventory 3 2 67 71 17))
              (make-inventory 2 1 66 70 16))

;; tests for valid inventory ordered according to number of items capable of
;;    being taken
;; 1 item
(check-expect (valid-inventory? (make-inventory 0 0 0 4 7)) true)
(check-expect (valid-inventory? (make-inventory 0 1 0 4 0)) true)
;; 2 items
(check-expect (valid-inventory? (make-inventory 0 0 0 5 0)) true)
(check-expect (valid-inventory? (make-inventory 0 1 0 4 7)) true)
(check-expect (valid-inventory? (make-inventory 0 2 0 5 0)) true)
;; 3 items
(check-expect (valid-inventory? (make-inventory 2 3 1 4 0)) true)
(check-expect (valid-inventory? (make-inventory 2 3 4 4 0)) true)
(check-expect (valid-inventory? (make-inventory 2 0 6 6 0)) true)
;; 4 items
(check-expect (valid-inventory? (make-inventory 2 3 5 5 0)) true)
(check-expect (valid-inventory? (make-inventory 3 3 4 4 7)) true)
(check-expect (valid-inventory? (make-inventory 3 3 0 5 7)) true)
;; 5 items
(check-expect (valid-inventory? (make-inventory 3 2 5 5 7)) true)
(check-expect (valid-inventory? (make-inventory 3 2 9 12 7)) true)
(check-expect (valid-inventory? (make-inventory 3 2 4 20 7)) true)
(check-expect (valid-inventory? (make-inventory 3 2 67 71 17)) true)
