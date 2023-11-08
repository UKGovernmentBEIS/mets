DROP FUNCTION IF EXISTS update_payment_fee_amount;

CREATE FUNCTION update_payment_fee_amount(competent_authority TEXT, request_type TEXT, fee_type TEXT, new_amount numeric)
RETURNS void AS $$
with f_tmp as (
select f.*
from request_payment_fee_method fm
inner join request_payment_fee f on fm.id = f.fee_method_id
where fm.competent_authority = update_payment_fee_amount.competent_authority
and fm.request_type = update_payment_fee_amount.request_type
and f.type = update_payment_fee_amount.fee_type
)
update request_payment_fee f
set amount = update_payment_fee_amount.new_amount
from f_tmp ftmp
where f.fee_method_id = ftmp.fee_method_id and
f.type = ftmp.type;
$$ LANGUAGE SQL;