package io.zeebe

import io.zeebe.client.ZeebeClient
import io.zeebe.client.api.response.ActivatedJob
import io.zeebe.client.api.worker.JobClient
import io.zeebe.client.api.worker.JobWorker
import java.time.Duration

object EchoWorker {

    @JvmStatic
    fun main(args: Array<String>) {
        println("Configuring Zeebe client...")
        val client = buildClient()

        println("Starting worker...")
        val worker = startWorker(client)

        Runtime.getRuntime().addShutdownHook(Thread {
            println("Stopping worker...")
            worker?.close()
            client?.close()
            println("Client closed")
        })
    }

    private fun handleJob(client: JobClient, job: ActivatedJob) {
        println("Executing Job ${job.key}...")
        when (job.customHeaders["action"] ?: "complete") {
            "complete" -> {
                client.newCompleteCommand(job.key)
                    .variables(job.customHeaders["variables"] ?: job.variables)
                    .send()
                println("Job ${job.key} completed")
            }
            "fail" -> {
                client.newFailCommand(job.key)
                    .retries(job.retries - 1)
                    .errorMessage("Echo worker fail message")
                    .send()
                println("Job ${job.key}, fail command sent")
            }
            "error" -> {
                client.newThrowErrorCommand(job.key)
                    .errorCode(job.customHeaders["errorCode"] ?: "error.code")
                    .errorMessage("Echo worker error message")
                    .send()
                println("Job ${job.key}, throw error command sent")
            }
        }
    }

    private fun startWorker(client: ZeebeClient): JobWorker =
        client.newWorker()
            .jobType(System.getenv("ZEEBE_CLIENT_WORKER_DEFAULTTYPE") ?: throw Exception("ZEEBE_CLIENT_WORKER_DEFAULTTYPE is required"))
            .handler(EchoWorker::handleJob)
            .open()

    private fun buildClient(): ZeebeClient =
        ZeebeClient.newClientBuilder()
            .brokerContactPoint(System.getenv("ZEEBE_CLIENT_BROKER_CONTACTPOINT") ?: throw Exception("ZEEBE_CLIENT_BROKER_CONTACTPOINT is required"))
            .numJobWorkerExecutionThreads(System.getenv("ZEEBE_CLIENT_WORKER_THREADS")?.toInt() ?: 1)
            .defaultJobWorkerMaxJobsActive(System.getenv("ZEEBE_CLIENT_WORKER_MAXJOBSACTIVE")?.toInt() ?: 32)
            .defaultJobWorkerName(System.getenv("ZEEBE_CLIENT_WORKER_NAME") ?: "default")
            .defaultJobTimeout(
                System.getenv("ZEEBE_CLIENT_JOB_TIMEOUT")
                    ?.toLong()?.toJavaDurationMillis()
                    ?: Duration.ofMinutes(5)
            )
            .defaultJobPollInterval(
                System.getenv("ZEEBE_CLIENT_JOB_POLLINTERVAL")
                    ?.toLong()?.toJavaDurationMillis()
                    ?: Duration.ofMillis(100)
            )
            .defaultMessageTimeToLive(
                System.getenv("ZEEBE_CLIENT_MESSAGE_TIMETOLIVE")
                    ?.toLong()?.toJavaDurationMillis()
                    ?: Duration.ofHours(1)
            )
            .defaultRequestTimeout(
                System.getenv("ZEEBE_CLIENT_REQUESTTIMEOUT")
                    ?.toLong()?.toJavaDurationMillis()
                    ?: Duration.ofSeconds(20)
            )
            .build()

    private fun Long.toJavaDurationMillis(): Duration = Duration.ofMillis(this)
}