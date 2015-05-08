select
  n.name node,
  n.version version,
  hs.ukey hs,
  ipr.name ip_addr_role,
  nip.ip_address ip_addr,
  niprs.ukey nip_rs,
  sip.number port,
  ers.ukey e_rs
from
  service_instance si,
  node n left outer join health_status hs on n.health_status_id = hs.id,
  ip_address_role ipr,
  service_instance_port sip,
  node_ip_address nip left outer join rotation_status niprs on nip.rotation_status_id = niprs.id,
  endpoint e left outer join rotation_status ers on e.rotation_status_id = ers.id
where
  si.ukey = 'expweb-prod-5551'
  and n.service_instance_id = si.id
  and nip.node_id = n.id
  and nip.ip_address_role_id = ipr.id
  and nip.rotation_status_id = niprs.id
  and e.node_ip_address_id = nip.id
  and e.service_instance_port_id = sip.id
order by
  n.name,
  ipr.name,
  sip.number
  ;
