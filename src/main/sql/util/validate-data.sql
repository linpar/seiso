-- =====================================================================================================================
-- validate-data.sql
-- 
-- Copyright 2013-2015 the original author or authors.
-- 
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- 
-- Willie Wheeler (wwheeler@expedia.com)
-- 
-- The schema includes diamond dependencies. The queries here look for data inconsistencies that can arise with diamond
-- dependencies.
-- =====================================================================================================================

-- This check looks for service instance mismatches for the following diamond dependency:
-- 
--     si
--   /    \
-- node   ipr
--   \    /
--    nip
-- 
select
  nip.id nip,
  node.id node,
  ipr.id ipr,
  node_si.id node_si,
  ipr_si.id ipr_si,
  (node_si.id = ipr_si.id) same
from
  node_ip_address nip,
  node,
  service_instance node_si,
  ip_address_role ipr,
  service_instance ipr_si
where
  nip.node_id = node.id and
  node.service_instance_id = node_si.id and
  nip.ip_address_role_id = ipr.id and
  ipr.service_instance_id = ipr_si.id and
  (node_si.id != ipr_si.id)
order by
  nip.id;

-- This check looks for service instance mismatches for the following diamond dependency:
-- 
--     si
--   /    \
-- node    |
--  |     sip
-- nip     |
--   \    /
--  endpoint
-- 
select
  e.id endpoint,
  nip.id nip,
  node.id node,
  sip.id sip,
  node_si.id node_si,
  node_si.ukey node_si_ukey,
  sip_si.id sip_si,
  sip_si.ukey sip_si_ukey,
  (node_si.id = sip_si.id) same
from
  endpoint e,
  node_ip_address nip,
  node,
  service_instance node_si,
  service_instance_port sip,
  service_instance sip_si
where
  e.node_ip_address_id = nip.id and
  nip.node_id = node.id and
  node.service_instance_id = node_si.id and
  e.service_instance_port_id = sip.id and
  sip.service_instance_id = sip_si.id and
  (node_si.id != sip_si.id)
order by
  e.id;

-- This check looks for service instance mismatches for the following diamond dependency:
-- 
--     si
--   /    \
-- ipr     |
--  |     sip
-- nip     |
--   \    /
--  endpoint
-- 
select
  e.id endpoint,
  nip.id nip,
  ipr.id ipr,
  sip.id sip,
  ipr_si.id ipr_si,
  ipr_si.ukey ipr_si_ukey,
  sip_si.id sip_si,
  sip_si.ukey sip_si_ukey,
  (ipr_si.id = sip_si.id) same
from
  endpoint e,
  node_ip_address nip,
  ip_address_role ipr,
  service_instance ipr_si,
  service_instance_port sip,
  service_instance sip_si
where
  e.node_ip_address_id = nip.id and
  nip.ip_address_role_id = ipr.id and
  ipr.service_instance_id = ipr_si.id and
  e.service_instance_port_id = sip.id and
  sip.service_instance_id = sip_si.id and
  (ipr_si.id != sip_si.id)
order by
  e.id;
