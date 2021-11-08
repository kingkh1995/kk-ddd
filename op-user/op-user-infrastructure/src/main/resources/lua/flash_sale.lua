-- 秒杀redis脚本
local key = KEYS[1]
local lkey = key .. '_limit'
local ukey = key .. '_user'
local ckey = key .. '_context'
local hashkey = KEYS[2]
-- 参与脚本运算的参数需要作为KEYS传递，这样才不会被序列化。
local number = KEYS[3]
local context = KEYS[4]
local stock = tonumber(redis.call('GET', key))
local limit = tonumber(redis.call('GET', lkey))
if stock == nil then
    -- 未开始秒杀
    return nil
elseif (stock >= limit) then
    local owned = tonumber(redis.call('HGET', ukey, hashkey))
    if (owned == nil or owned <= limit - number) then
        -- 大于0 秒杀成功
        redis.call('DECRBY', key, number)
        redis.call('LPUSH', ckey, context)
        return redis.call('HINCRBY', ukey, hashkey, number)
    else
        -- -1 秒杀失败，秒杀成功数量已达限制
        return -1
    end
else
    -- 等于0 秒杀失败，库存数量不足
    return 0
end