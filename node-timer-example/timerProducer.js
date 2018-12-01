const AMQP = require('amqplib')
const startProducer = async (conn, messageCount) => {
    const channel = await conn.createConfirmChannel()
    const producerQueue = 'timerProducerQ'
    // 定义队列
    await channel.assertQueue(producerQueue, { durable: true })
    for (let i = 0; i < messageCount; i += 1) {
        const message = {
            time: Date.now(),
            ok: true
        }
        await channel.sendToQueue(producerQueue, Buffer.from(JSON.stringify(message)), {
            mandatory: true,
            headers: { retrySum: 1 } // TODO 预留，重试时使用
        }, (err) => {
            if (err) {
                return console.error(`sendToQueue ${producerQueue} error: ${err.toString()}`)
            }
            console.log(`sendToQueue ${producerQueue} success: ${JSON.stringify(message)}`)
        })
        console.log(`total: ${messageCount}, send message progress ${i} ...`)
    }
}

const startConsumer = async (conn, prefetch) => {
    const channel = await conn.createConfirmChannel()
    const consumerQueue = 'timerConsumerQ'
    // 定义队列
    await channel.assertQueue(consumerQueue, { durable: true })
    // 预加载1个消息
    await channel.prefetch(parseInt(prefetch))
    // 监听并消费通知队列
    await channel.consume(consumerQueue, async (msg) => {
        try {
            const message = JSON.parse(msg.content.toString())
            console.log('consumer message: ', message)
        } catch (e) {
            console.error('consumer err: ' + e.toString())
        } finally {
            channel.ack(msg)
            console.log('consumer message success ...')
        }
    }, { noAck: false })
    console.log('start consumer success....')
}

const main = async (messageCount, prefetch) => {
    const conn = await AMQP.connect('amqp://localhost')
    startConsumer(conn, prefetch).then()
    startProducer(conn, messageCount).then()
}

main(1, 1).then()
