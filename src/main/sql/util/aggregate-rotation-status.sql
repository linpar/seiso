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


-- =====================================================================================================================
-- Work
-- =====================================================================================================================

  expweb-trunk-linux-5551 has service instance ID 405.

its nodes:
Node ID 981: chelwbttqau001-5551
Node ID 982: chelwbttqau002-5551
Node ID 983: chelwbttqau003-5551
Node ID 984: chelwbttqau004-5551

NIPS:

select
  nip.*
from
  node_ip_address nip,
  node n,
  service_instance si
where
  nip.node_id = n.id
  and n.service_instance_id = si.id
  and si.ukey = 'expweb-trunk-linux-5551'
  ;

ENDPOINTS:

select
  e.*
from
  endpoint e,
  node_ip_address nip,
  node n,
  service_instance si
where
  e.node_ip_address_id = nip.id
  and nip.node_id = n.id
  and n.service_instance_id = si.id
  and si.ukey = 'expweb-trunk-linux-5551'
  ;
