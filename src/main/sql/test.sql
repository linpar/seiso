select
  n.id node_id,
  n.name node,
  nip.id nip_id,
  nip.ip_address ip
from
  node n,
  node_ip_address nip,
  rotation_status nars,
  rotation_status nipars
where
  nip.node_id = n.id
  and nip.aggregate_rotation_status_id = nars.id
  and nars.ukey = 'disabled'
  and n.aggregate_rotation_status_id = nipars.id
  and nipars.ukey = 'unknown';
