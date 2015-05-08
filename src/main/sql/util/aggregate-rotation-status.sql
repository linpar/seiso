-- This doesn't compute the actual aggregate rotation status.
-- Instead it gets the data that we need to compute it in the domain layer.

-- No need for outer joins on the nodes, node IP addresses or the endpoints, because the app ensures that these exist.
-- Rotation statuses can be null though.
select
  n.id node_id,
  nip.id nip_id,
  niprs.ukey niprs_key,
  ers.ukey ers_key,
  count(*) num_endpoints
from
  endpoint e,
  rotation_status ers,
  node_ip_address nip,
  rotation_status niprs,
  node n,
  service_instance si
where
  e.node_ip_address_id = nip.id
  and e.rotation_status_id = ers.id
  and nip.node_id = n.id
  and nip.rotation_status_id = niprs.id
  and n.service_instance_id = si.id
  and si.ukey = 'expweb-prod-5552'
group by
  n.id,
  nip.id,
  ers.id
  ;
